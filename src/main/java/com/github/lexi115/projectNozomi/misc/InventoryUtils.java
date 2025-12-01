package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for inventory-related operations.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class InventoryUtils {

    /**
     * Removes a certain amount of items matching the specified material.
     *
     * @param inventory The inventory.
     * @param material  The material to match.
     * @param amount    The amount to remove.
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @throws IllegalArgumentException If specified amount is negative.
     * @since 1.0
     */
    public boolean removeItems(
            final @NonNull Inventory inventory,
            final Material material,
            final int amount
    ) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be a positive integer!");
        }
        var itemStack = new ItemStack(material, 1);
        if (!inventory.containsAtLeast(itemStack, amount)) {
            return false;
        }
        return inventory.removeItem(new ItemStack(material, amount)).isEmpty();
    }
}
