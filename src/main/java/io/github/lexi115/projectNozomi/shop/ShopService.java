package io.github.lexi115.projectNozomi.shop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.lexi115.projectNozomi.ProjectNozomi;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

@Singleton
public class ShopService {

    private final ProjectNozomi plugin;

    private final Shop shop;

    @Inject
    public ShopService(final ProjectNozomi plugin, final Shop shop) {
        this.plugin = plugin;
        this.shop = shop;
    }

    public Shop loadShopFromConfig() {
        var section = plugin.getShopConfig().getConfigurationSection("shop.items");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        shop.clearItems();
        section.getKeys(false).forEach(key -> {
            var item = ShopItem.builder()
                    .id(key)
                    .name(section.getString(key + ".name"))
                    .material(Material.matchMaterial(section.getString(key + ".material", Material.AIR.name())))
                    .amount(section.getInt(key + ".amount"))
                    .rewards(parseItemRewards(section.getStringList(key + ".rewards")))
                    .build();
            shop.addItem(item);
        });
        return shop;
    }

    public Collection<ShopItem> loadDailyItemsFromConfig() {
        var configDailyItems = plugin.getDailyItemsConfig().getStringList("daily-items");
        var shopItems = shop.getItems();
        shop.clearDailyItems();
        configDailyItems.forEach(itemId -> {
            for (var item : shopItems) {
                if (item.getId().equals(itemId)) {
                    shop.addDailyItem(item);
                    break;
                }
            }
        });
        var dailyItems = shop.getDailyItems();
        if (configDailyItems.isEmpty() || dailyItems.isEmpty()) {
            refreshDailyItems();
            saveDailyItemsInConfig();
            return dailyItems;
        }
        return dailyItems;
    }

    public void saveDailyItemsInConfig() {
        var idList = shop.getDailyItems().stream().map(ShopItem::getId).toList();
        var dailyItemsConfig = plugin.getDailyItemsConfig();
        dailyItemsConfig.set("daily-items", idList);
        try {
            dailyItemsConfig.save(plugin.getDataFolder() + "/daily.yml");
        } catch (IOException e) {
            throw new SaveFileException(e);
        }
    }

    public void refreshDailyItems() {
        int dailyItemsAmount = plugin.getConfig().getInt("daily-items.amount", 3);
        var totalItems = shop.getTotalItems();
        if (totalItems < dailyItemsAmount) {
            throw new NotEnoughItemsException();
        }
        var randomizer = new Random();
        ShopItem randomItem;
        shop.clearDailyItems();
        while (shop.getTotalDailyItems() < dailyItemsAmount) {
            randomItem = shop.getItem(randomizer.nextInt(totalItems));
            shop.addDailyItem(randomItem);
        }
    }

    public Collection<ShopItem> getDailyItems() {
        return shop.getDailyItems();
    }

    public void sellItem(final Player player, final ShopItem shopItem) {
        var placeholders = new HashMap<String, String>();
        placeholders.put("player", player.getName());
        for (var reward : shopItem.getRewards()) {
            reward.give(player, placeholders);
        }
    }

    private List<Reward> parseItemRewards(final List<String> rewardStrings) {
        return rewardStrings.stream().map(this::parseRewardFromString).toList();
    }

    private Reward parseRewardFromString(final String string) {
        if (string.startsWith("cmd:")) {
            return new CommandReward(string.replaceFirst("cmd:", "").trim());
        }
        throw new InvalidRewardException();
    }
}
