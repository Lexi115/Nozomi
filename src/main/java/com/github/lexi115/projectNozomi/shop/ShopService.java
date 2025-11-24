package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.InventoryUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.SaveFileException;
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

@Singleton
public class ShopService {

    private final ProjectNozomi plugin;

    private final Shop shop;

    private final RewardUtils rewardUtils;

    private final InventoryUtils inventoryUtils;

    @Inject
    public ShopService(
            final ProjectNozomi plugin,
            final Shop shop,
            final RewardUtils rewardUtils,
            final InventoryUtils inventoryUtils
    ) {
        this.plugin = plugin;
        this.shop = shop;
        this.rewardUtils = rewardUtils;
        this.inventoryUtils = inventoryUtils;
    }

    public void loadShopFromConfig() {
        var section = plugin.getShopConfig().getConfigurationSection("shop.items");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        shop.clearItems();
        section.getKeys(false).forEach(key -> shop.addItem(parseItemFromConfig(section, key)));
    }

    public void loadDailyItemsFromConfig() {
        var configDailyItemsIds = plugin.getDailyItemsConfig().getStringList("daily-items");
        var itemsMap = shop.getItems().stream().collect(Collectors.toMap(ShopItem::getId, i -> i));
        shop.clearDailyItems();
        configDailyItemsIds.forEach(itemId -> {
            var shopItem = itemsMap.get(itemId);
            if (shopItem != null) {
                shop.addDailyItem(shopItem);
            }
        });
        var dailyItems = shop.getDailyItems();
        if (configDailyItemsIds.isEmpty() || dailyItems.isEmpty()) {
            refreshDailyItems();
            saveDailyItemsInConfig();
        }
    }

    public void saveDailyItemsInConfig() {
        var idList = shop.getDailyItems().stream().map(ShopItem::getId).toList();
        var dailyItemsConfig = plugin.getDailyItemsConfig();
        dailyItemsConfig.set("daily-items", idList);
        try {
            dailyItemsConfig.save(plugin.getDataFolder() + "/daily.yml");
        } catch (IOException e) {
            throw new SaveFileException(e);
        }
    }

    public void refreshDailyItems() {
        int dailyItemsAmount = plugin.getConfig().getInt("daily-items.amount", 3);
        var shopItems = shop.getItems();
        if (shop.getTotalItems() < dailyItemsAmount) {
            throw new NotEnoughItemsException();
        }
        Collections.shuffle(shopItems);
        shop.clearDailyItems();
        for (int i = 0; i < dailyItemsAmount; i++) {
            shop.addDailyItem(shopItems.get(i));
        }
    }

    public boolean sellItem(final @NonNull Player player, final @NonNull ShopItem item) {
        if (!inventoryUtils.removeItems(player.getInventory(), item.getMaterial(), item.getAmount())) {
            return false;
        }
        var placeholders = new PlaceholderMap()
                .set("player", player.getName())
                .map();
        item.getRewards().forEach(reward -> {
            if (!reward.give(player, placeholders)) {
                throw new SellItemException("Could not give all rewards!");
            }
        });
        return true;
    }

    public Collection<ShopItem> getItems() {
        return shop.getItems();
    }

    public int getTotalItems() {
        return shop.getTotalItems();
    }

    public Collection<ShopItem> getDailyItems() {
        return shop.getDailyItems();
    }

    public int getTotalDailyItems() {
        return shop.getTotalDailyItems();
    }

    private ShopItem parseItemFromConfig(final @NonNull ConfigurationSection section, final String key) {
        return ShopItem.builder()
                .id(key)
                .name(section.getString(key + ".name"))
                .material(Material.matchMaterial(section.getString(key + ".material", Material.AIR.name())))
                .amount(section.getInt(key + ".amount", 1))
                .rewards(rewardUtils.parseFrom(section.getStringList(key + ".rewards")))
                .build();
    }
}
