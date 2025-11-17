package io.github.lexi115.projectNozomi.shop;

import org.bukkit.configuration.file.FileConfiguration;

public class ShopService {
    public Shop loadShopFromConfig(final FileConfiguration shopConfig) {
        var shop = new Shop();
        var itemsSection = shopConfig.getConfigurationSection("items");
        if (itemsSection == null) {
            throw new ShopNotFoundException();
        }
        itemsSection.getKeys(false).forEach(key -> {
            var item = ShopItem.builder()
                    .name(itemsSection.getString(key + ".name"))
                    .amount(itemsSection.getInt(key + ".amount"))
                    .build();
            shop.addItem(item);
        });
        return shop;
    }
}
