package io.github.lexi115.projectNozomi.shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

public class ShopService {

    private final JavaPlugin plugin;

    private final Shop shop;

    public ShopService(final JavaPlugin plugin, final Shop shop) {
        this.plugin = plugin;
        this.shop = shop;
    }

    public Shop loadShopFromConfig(final FileConfiguration shopConfig) {
        var section = shopConfig.getConfigurationSection("items");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        shop.clearItems();
        section.getKeys(false).forEach(key -> {
            var item = ShopItem.builder()
                    .id(key)
                    .name(section.getString(key + ".name"))
                    .amount(section.getInt(key + ".amount"))
                    .build();
            shop.addItem(item);
        });
        return shop;
    }

    public Collection<ShopItem> loadDailyItemsFromConfig(final FileConfiguration dailyItemsConfig) {
        var dailyItemsList = dailyItemsConfig.getStringList("daily-items");
        if (dailyItemsList.isEmpty()) {
            var dailyItems = refreshDailyItems();
            saveDailyItemsInConfig(dailyItemsConfig);
            return dailyItems;
        }
        shop.clearDailyItems();
        shop.getItems().forEach(item -> {
            if (dailyItemsList.contains(item.getId())) {
                shop.addDailyItem(item);
            }
        });
        return shop.getDailyItems();
    }

    public void saveDailyItemsInConfig(final FileConfiguration dailyItemsConfig) {
        var idList = shop.getDailyItems().stream().map(ShopItem::getId).toList();
        dailyItemsConfig.set("daily-items", idList);
        try {
            dailyItemsConfig.save(plugin.getDataFolder() + "/daily.yml");
        } catch (IOException e) {
            throw new SaveFileException(e);
        }
    }

    public Collection<ShopItem> getDailyItems() {
        return shop.getDailyItems();
    }

    public Collection<ShopItem> refreshDailyItems() {
        int dailyItemsAmount = plugin.getConfig().getInt("daily-items.amount", 3);
        var totalItems = shop.getTotalItems();
        if (totalItems < dailyItemsAmount) {
            throw new NotEnoughItemsException();
        }
        var randomizer = new Random();
        ShopItem randomItem;
        shop.clearDailyItems();
        while (shop.getTotalDailyItems() < dailyItemsAmount) {
            randomItem = shop.getItem(randomizer.nextInt(totalItems));
            shop.addDailyItem(randomItem);
        }
        return shop.getDailyItems();
    }
}
