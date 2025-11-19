package io.github.lexi115.projectNozomi.shop.gui;

import io.github.lexi115.projectNozomi.shop.Item;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemMapper {

    public ItemStack toItemStack(final Item item) {
        var itemStack = new ItemStack(item.getMaterial(), 1);
        var itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        if (itemMeta != null) {
            itemMeta.setItemName(item.getName());
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
