package io.github.lexi115.projectNozomi.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Shop {
    private List<ShopItem> items;
    private Set<ShopItem> dailyItems;

    public Shop() {
        this.items = new ArrayList<>();
        this.dailyItems = new HashSet<>();
    }

    public void addItem(final ShopItem item) {
        items.add(item);
    }

    public void clearItems() {
        items.clear();
    }

    public int getTotalItems() {
        return items.size();
    }
}
