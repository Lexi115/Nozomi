package io.github.lexi115.projectNozomi.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemMapper {
    public ItemStack toItemStack(final ShopItem shopItem) {
        var material = Material.matchMaterial(shopItem.getName());
        if (material == null) {
            throw new InvalidMappingException();
        }
        return new ItemStack(material, 1);
    }
}
