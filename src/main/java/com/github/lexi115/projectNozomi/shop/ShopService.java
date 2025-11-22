package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.InventoryUtils;
import com.github.lexi115.projectNozomi.misc.SaveFileException;
import com.github.lexi115.projectNozomi.shop.rewards.RewardUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.lexi115.projectNozomi.ProjectNozomi;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;
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
        section.getKeys(false).forEach(key -> {
            var item = ShopItem.builder()
                    .id(key)
                    .name(section.getString(key + ".name"))
                    .material(Material.matchMaterial(section.getString(key + ".material", Material.AIR.name())))
                    .amount(section.getInt(key + ".amount", 1))
                    .rewards(rewardUtils.parseRewards(section.getStringList(key + ".rewards")))
                    .build();
            shop.addItem(item);
        });
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

    public boolean sellItem(final @NonNull Player player, final @NonNull ShopItem shopItem) {
        var playerInventory = player.getInventory();
        var itemMaterial = shopItem.getMaterial();
        var itemAmount = shopItem.getAmount();
        var placeholders = new HashMap<String, String>();
        if (!inventoryUtils.removeItems(playerInventory, itemMaterial, itemAmount)) {
            return false;
        }
        placeholders.put("player", player.getName());
        for (var reward : shopItem.getRewards()) {
            reward.give(player, placeholders);
        }
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
}
