package com.github.lexi115.projectNozomi.shop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Represents a shop full of items that can be sold by players. It is accessed through a
 * {@link com.github.lexi115.projectNozomi.shop.gui.ShopGui} and is generally refreshed periodically.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
@Getter
public class Shop {

    /**
     * All the items loaded in the shop.
     */
    private final List<ShopItem> items;

    /**
     * The currently chosen daily items.
     */
    private final Set<ShopItem> dailyItems;

    /**
     * An ID that changes each daily items refresh. Its value is checked to reset players' shop uses whenever they
     * access the shop after a new refresh.
     */
    @Setter
    private String refreshId;

    /**
     * Constructor.
     *
     * @since 1.0
     */
    @Inject
    public Shop() {
        this.items = new ArrayList<>();
        this.dailyItems = new LinkedHashSet<>();
        this.refreshId = null;
    }

    /**
     * Adds an item to the shop.
     *
     * @param item The item to add.
     * @since 1.0
     */
    public void addItem(final ShopItem item) {
        items.add(item);
    }

    /**
     * Adds an item to the daily items set.
     *
     * @param item The item to add.
     * @since 1.0
     */
    public void addDailyItem(final ShopItem item) {
        dailyItems.add(item);
    }

    /**
     * Removes every item in the shop.
     *
     * @since 1.0
     */
    public void clearItems() {
        items.clear();
    }

    /**
     * Removes every daily item in the shop.
     *
     * @since 1.0
     */
    public void clearDailyItems() {
        dailyItems.clear();
    }

    /**
     * Returns the total number of items in the shop.
     *
     * @return the number of items.
     * @since 1.0
     */
    public int getTotalItems() {
        return items.size();
    }

    /**
     * Returns the total number of daily items in the shop.
     *
     * @return the number of daily items.
     * @since 1.0
     */
    public int getTotalDailyItems() {
        return dailyItems.size();
    }

    /**
     * Generates a new refresh ID for this shop.
     *
     * @since 1.0
     */
    public void regenerateRefreshId() {
        refreshId = UUID.randomUUID().toString();
    }
}
