package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Mapper class that converts this plugin's {@link Item} objects into Bukkit's {@link ItemStack} objects that can
 * be put in in-game inventories.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class ItemMapper {

    /**
     * The string utility class.
     */
    private final StringUtils stringUtils;

    /**
     * Constructor.
     *
     * @param stringUtils The string utility class.
     * @since 1.0
     */
    @Inject
    public ItemMapper(final StringUtils stringUtils) {
        this.stringUtils = stringUtils;
    }

    /**
     * Maps an {@link Item} to an {@link ItemStack}.
     *
     * @param item The item to convert.
     * @return The correspondent {@link ItemStack}.
     * @since 1.0
     */
    public ItemStack toItemStack(final @NonNull Item item) {
        return toItemStack(item, 1, Map.of());
    }

    /**
     * Maps an {@link Item} to an {@link ItemStack}.
     *
     * @param item   The item to convert.
     * @param amount The amount inside the stack.
     * @return The correspondent {@link ItemStack}.
     * @since 1.0
     */
    public ItemStack toItemStack(final @NonNull Item item, final int amount) {
        return toItemStack(item, amount, Map.of());
    }

    /**
     * Maps an {@link Item} to an {@link ItemStack}.
     *
     * @param item The item to convert.
     * @param amount The amount inside the stack.
     * @param placeholders The placeholders map.
     * @return The correspondent {@link ItemStack}.
     * @since 1.0
     */
    public ItemStack toItemStack(
            final @NonNull Item item,
            final int amount,
            final @NonNull Map<String, String> placeholders
    ) {
        var itemStack = new ItemStack(item.getMaterial(), amount);
        var itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        if (itemMeta != null) {
            var name = item.getName();
            if (name != null) {
                itemMeta.setItemName(stringUtils.format(name, placeholders));
            }
            var displayName = item.getDisplayName();
            if (displayName != null) {
                itemMeta.setDisplayName(stringUtils.format(displayName, placeholders));
            }
            var lore = item.getLore().stream().map(row -> stringUtils.format(row, placeholders)).toList();
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
