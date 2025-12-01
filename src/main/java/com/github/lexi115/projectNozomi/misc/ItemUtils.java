package com.github.lexi115.projectNozomi.misc;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for item-related operations.
 *
 * @author Lexi115
 * @since 1.0
 */
public class ItemUtils {

    /**
     * Drop an item stack at the specified location. If the item amount exceeds the maximum stack size,
     * multiple drops will spawn.
     *
     * @param item     The item stack to drop.
     * @param world    The target world.
     * @param location The location where the drop entity should be spawned.
     * @since 1.0
     */
    public void dropItem(
            final @NonNull ItemStack item,
            final @NonNull World world,
            final @NonNull Location location
    ) {
        var maxStack = item.getMaxStackSize();
        var itemAmount = item.getAmount();
        while (itemAmount > maxStack) {
            item.setAmount(maxStack);
            world.dropItem(location, item);
            itemAmount -= maxStack;
        }
        if (itemAmount > 0) {
            item.setAmount(itemAmount);
            world.dropItem(location, item);
        }
    }
}
