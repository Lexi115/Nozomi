package com.github.lexi115.projectNozomi.shop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Singleton
@Getter
public class Shop {

    private final List<ShopItem> items;

    private final Set<ShopItem> dailyItems;

    @Setter
    private String refreshId;

    @Inject
    public Shop() {
        this.items = new ArrayList<>();
        this.dailyItems = new LinkedHashSet<>();
        this.refreshId = null;
    }

    public ShopItem getItem(final int index) {
        return items.get(index);
    }

    public void addItem(final ShopItem item) {
        items.add(item);
    }

    public void addDailyItem(final ShopItem item) {
        dailyItems.add(item);
    }

    public void clearItems() {
        items.clear();
    }

    public void clearDailyItems() {
        dailyItems.clear();
    }

    public int getTotalItems() {
        return items.size();
    }

    public int getTotalDailyItems() {
        return dailyItems.size();
    }

    public String regenerateRefreshId() {
        refreshId = UUID.randomUUID().toString();
        return refreshId;
    }
}
