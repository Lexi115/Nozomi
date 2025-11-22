package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Singleton
public class InventoryUtils {

    public boolean removeItems(final Inventory inventory, final Material material, final int amount) {
        var itemStack = new ItemStack(material, 1);
        if (!inventory.containsAtLeast(itemStack, amount)) {
            return false;
        }
        return inventory.removeItem(new ItemStack(material, amount)).isEmpty();
    }
}
