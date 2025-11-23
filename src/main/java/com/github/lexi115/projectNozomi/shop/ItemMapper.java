package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Singleton
public class ItemMapper {

    private final StringUtils stringUtils;

    @Inject
    public ItemMapper(final StringUtils stringUtils) {
        this.stringUtils = stringUtils;
    }

    public ItemStack toItemStack(final @NonNull Item item) {
        return toItemStack(item, 1, Map.of());
    }

    public ItemStack toItemStack(final @NonNull Item item, final int amount) {
        return toItemStack(item, amount, Map.of());
    }

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
                itemMeta.setItemName(stringUtils.colorize(stringUtils.fillPlaceholders(item.getName(), placeholders)));
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
