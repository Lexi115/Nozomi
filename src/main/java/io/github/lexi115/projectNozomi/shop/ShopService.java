package io.github.lexi115.projectNozomi.shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class ShopService {

    private final JavaPlugin plugin;

    public ShopService(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Shop loadShopFromConfig(final Shop existingShop, final FileConfiguration shopConfig) {
        var shop = existingShop == null ? new Shop() : existingShop;
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

    public Collection<ShopItem> refreshDailyItems(final Shop shop) {
        int maxDailyItems = plugin.getConfig().getInt("max-daily-items", 3);
        return shop.refreshDailyItems(maxDailyItems);
    }
}
