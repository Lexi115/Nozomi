package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.misc.ItemUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Reward that consists of giving items to a player.
 *
 * @author Lexi115
 * @since 1.0
 */
public class ItemReward implements Reward {

    /**
     * The item utility class.
     */
    private final ItemUtils itemUtils;

    /**
     * The item's material.
     */
    private final Material material;

    /**
     * The amount to give.
     */
    private final int amount;

    /**
     * Constructor.
     *
     * @param itemUtils The item utility class.
     * @param material  The item's material.
     * @param amount    The amount to give.
     */
    public ItemReward(final ItemUtils itemUtils, final Material material, final int amount) {
        this.itemUtils = itemUtils;
        this.material = material;
        this.amount = amount;
    }

    /**
     * Gives the reward to the specified player.
     *
     * @param player       The target player
     * @param placeholders The placeholders map.
     * @throws RewardGiveException if reward could not be given due to an error.
     * @since 1.0
     */
    @Override
    public void give(final @NonNull Player player, final @NonNull Map<String, String> placeholders) {
        if (material == null) {
            throw new RewardGiveException("Material is null");
        }
        if (amount < 0) {
            throw new RewardGiveException("Amount is negative");
        }
        var itemStack = new ItemStack(material, amount);
        var remainingItems = player.getInventory().addItem(itemStack);
        // If player inventory is full, drop the remaining items in front of player
        if (!remainingItems.isEmpty()) {
            remainingItems.forEach((index, item)
                    -> itemUtils.dropItem(item, player.getWorld(), player.getLocation()));
        }
    }
}
