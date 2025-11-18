package io.github.lexi115.projectNozomi.shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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
        var itemsSection = shopConfig.getConfigurationSection("items");
        if (itemsSection == null) {
            throw new ShopNotFoundException();
        }
        shop.clearItems();
        itemsSection.getKeys(false).forEach(key -> {
            var item = ShopItem.builder()
                    .name(itemsSection.getString(key + ".name"))
                    .amount(itemsSection.getInt(key + ".amount"))
                    .build();
            shop.addItem(item);
        });
        return shop;
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
