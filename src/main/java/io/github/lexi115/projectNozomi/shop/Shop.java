package io.github.lexi115.projectNozomi.shop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;

import java.util.*;

@Singleton
@Getter
public class Shop {

    private final List<ShopItem> items;

    private final Set<ShopItem> dailyItems;

    @Inject
    public Shop() {
        this.items = new ArrayList<>();
        this.dailyItems = new LinkedHashSet<>();
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
}
