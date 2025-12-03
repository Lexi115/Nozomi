package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.github.lexi115.projectNozomi.database.services.ShopUsesService;
import com.github.lexi115.projectNozomi.misc.InventoryUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.RuntimeIOException;
import com.github.lexi115.projectNozomi.shop.rewards.RewardUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Service class that handles every operation on the sell shop. It is also used to load the shop from config files
 * upon plugin startup.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class ShopService {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The shop.
     */
    private final Shop shop;

    /**
     * Service class that keeps track of players' shop uses.
     */
    private final ShopUsesService shopUsesService;

    /**
     * Utility class to give players rewards upon selling items.
     */
    private final RewardUtils rewardUtils;

    /**
     * Utility class for specific inventory operations.
     */
    private final InventoryUtils inventoryUtils;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param shop The shop that this service will manage.
     * @param shopUsesService The shop uses service.
     * @param rewardUtils The rewards utility class.
     * @param inventoryUtils The inventory utility class.
     * @since 1.0
     */
    @Inject
    public ShopService(
            final ProjectNozomi plugin,
            final Shop shop,
            final ShopUsesService shopUsesService,
            final RewardUtils rewardUtils,
            final InventoryUtils inventoryUtils
    ) {
        this.plugin = plugin;
        this.shop = shop;
        this.shopUsesService = shopUsesService;
        this.rewardUtils = rewardUtils;
        this.inventoryUtils = inventoryUtils;
    }

    /**
     * Loads the shop and its items by reading its config file (<code>shop.yml</code>).
     *
     * @see Shop
     * @see ShopItem
     * @since 1.0
     */
    public void loadShopFromConfig() {
        var section = plugin.getShopConfig().getConfigurationSection("shop.items");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        shop.clearItems();
        section.getKeys(false).forEach(key -> shop.addItem(parseItemFromConfig(section, key)));
    }

    private ShopItem parseItemFromConfig(final @NonNull ConfigurationSection section, final String key) {
        return ShopItem.builder()
                .id(key)
                .name(section.getString(key + ".name"))
                .displayName(section.getString(key + ".display-name"))
                .amount(section.getInt(key + ".amount", 1))
                .lore(section.getStringList(key + ".lore"))
                .material(Material.matchMaterial(section.getString(key + ".material", Material.AIR.name())))
                .rewards(rewardUtils.parseFrom(section.getStringList(key + ".rewards")))
                .build();
    }

    /**
     * Loads the previously chosen daily items by reading its config file (<code>daily.yml</code>). Daily items are
     * stored by their IDs: if one of them is no longer found upon next startup / reload, it is simply discarded
     * (doesn't appear in the shop GUI). If every ID turns out to be invalid or no ID is present at all, it forces a
     * daily item refresh.
     *
     * @see Shop
     * @since 1.0
     */
    public void loadDailyItemsFromConfig() {
        var config = plugin.getDailyItemsConfig();
        var configDailyItemsIds = config.getStringList("daily-items");
        var itemsMap = shop.getItems().stream().collect(Collectors.toMap(ShopItem::getId, i -> i));
        shop.clearDailyItems();
        configDailyItemsIds.forEach(itemId -> {
            var shopItem = itemsMap.get(itemId);
            if (shopItem != null) {
                shop.addDailyItem(shopItem);
            }
        });
        var configRefreshId = config.getString("refresh-id");
        shop.setRefreshId(configRefreshId);
        var dailyItems = shop.getDailyItems();
        if (configDailyItemsIds.isEmpty() || dailyItems.isEmpty()
                || configRefreshId == null || configRefreshId.isBlank()) {
            refreshDailyItems();
            saveDailyItemsInConfig();
        }
    }

    /**
     * Saves the current daily items in the <code>daily.yml</code> config file.
     *
     * @throws RuntimeIOException If an I/O error occurs.
     * @since 1.0
     */
    public void saveDailyItemsInConfig() {
        var idList = shop.getDailyItems().stream().map(ShopItem::getId).toList();
        var dailyItemsConfig = plugin.getDailyItemsConfig();
        dailyItemsConfig.set("daily-items", idList);
        dailyItemsConfig.set("refresh-id", shop.getRefreshId());
        try {
            dailyItemsConfig.save(plugin.getDataFolder() + "/daily.yml");
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Refreshes the daily items and regenerates the shop's <code>refreshId</code>.
     *
     * @throws NotEnoughShopItemsException If there aren't enough items in the shop to choose as daily items.
     * @since 1.0
     */
    public void refreshDailyItems() {
        int dailyItemsAmount = plugin.getConfig().getInt("daily-items.amount", 3);
        var shopItems = shop.getItems();
        if (shop.getTotalItems() < dailyItemsAmount) {
            throw new NotEnoughShopItemsException("Not enough shop items to choose as daily items!");
        }
        Collections.shuffle(shopItems);
        shop.clearDailyItems();
        for (int i = 0; i < dailyItemsAmount; i++) {
            shop.addDailyItem(shopItems.get(i));
        }
        shop.regenerateRefreshId();
    }

    /**
     * Makes a player sell an item and gives one or more rewards (defined in the <code>shop.yml</code> config) if
     * the operation is successful.
     *
     * @param player The player selling the item.
     * @param item The item to sell.
     * @throws InvalidShopItemAmountException If the shop item amount is negative.
     * @throws NoShopUsesLeftException If player has no shop uses left.
     * @throws InsufficientAmountException If player doesn't have enough items to sell in his inventory.
     * @since 1.0
     */
    public void sellItem(final @NonNull Player player, final @NonNull ShopItem item) {
        var amount = item.getAmount();
        if (amount < 0) {
            throw new InvalidShopItemAmountException(amount);
        }
        var playerShopUses = shopUsesService.getPlayerUses(player);
        if (playerShopUses == 0) {
            throw new NoShopUsesLeftException("No more shop uses for this player!");
        }
        if (!inventoryUtils.removeItems(player.getInventory(), item.getMaterial(), amount)) {
            throw new InsufficientAmountException("Not enough items in inventory!");
        }
        var placeholders = new PlaceholderMap()
                .set("player", player.getName());
        item.getRewards().forEach(reward -> reward.give(player, placeholders.map()));
        var newShopUses = playerShopUses != ShopUses.UNLIMITED ? playerShopUses - 1 : ShopUses.UNLIMITED;
        shopUsesService.savePlayerUses(player, newShopUses, shop.getRefreshId());
    }

    /**
     * Returns all shop items.
     *
     * @return The items in the shop.
     * @since 1.0
     */
    public Collection<ShopItem> getItems() {
        return shop.getItems();
    }

    /**
     * Returns the total number of shop items.
     *
     * @return The number of items in the shop.
     * @since 1.0
     */
    public int getTotalItems() {
        return shop.getTotalItems();
    }

    /**
     * Returns the current daily items.
     *
     * @return The daily items.
     * @since 1.0
     */
    public Collection<ShopItem> getDailyItems() {
        return shop.getDailyItems();
    }

    /**
     * Returns the total number of daily items.
     *
     * @return The number of daily items.
     * @since 1.0
     */
    public int getTotalDailyItems() {
        return shop.getTotalDailyItems();
    }
}
