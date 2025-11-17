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

    public Collection<ShopItem> refreshDailyItems(final Integer amount) {
        var totalItems = items.size();
        if (totalItems < amount) {
            throw new NotEnoughItemsException();
        }
        var randomizer = new Random();
        ShopItem randomItem;
        dailyItems.clear();
        while (dailyItems.size() < amount) {
            randomItem = items.get(randomizer.nextInt(totalItems));
            dailyItems.add(randomItem);
        }
        return dailyItems;
    }
}
