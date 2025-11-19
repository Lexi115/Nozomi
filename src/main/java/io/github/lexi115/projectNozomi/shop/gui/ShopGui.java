package io.github.lexi115.projectNozomi.shop.gui;

import io.github.lexi115.projectNozomi.shop.ShopItem;
import io.github.lexi115.projectNozomi.shop.ShopService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class ShopGui implements Listener {

    private final JavaPlugin plugin;

    private final Inventory shopInventory;

    private final ShopService shopService;

    private final ItemMapper itemMapper;

    private final Map<Integer, ShopItem> slotsMap = new HashMap<>();

    private final Player player;

    private final Integer page;

    private final ShopGuiManager manager;

    private Integer totalPages = 0;

    private final int PREVIOUS_PAGE_SLOT = 45;

    private final int NEXT_PAGE_SLOT = 53;

    public ShopGui(
            final JavaPlugin plugin,
            final ShopService shopService,
            final ItemMapper itemMapper,
            final Player player,
            final Integer page,
            final ShopGuiManager manager
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.player = player;
        this.page = page;
        this.manager = manager;
        this.shopInventory = createShopInventory();
        registerEvents();
    }

    public void openInventory() {
        player.openInventory(shopInventory);
    }

    public void closeInventory() {
        player.closeInventory();
    }

    public void registerEvents() {
        var pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }

    private Inventory createShopInventory() {
        var inventory = Bukkit.createInventory(null, 54, "Yo");
        var dailyItems = shopService.getDailyItems();
        var dailyItemsSize = dailyItems.size();
        var pageSize = 2;
        totalPages = (int) Math.ceil(dailyItemsSize / (double) pageSize);
        if (page > totalPages) {
            throw new InvalidPageException();
        }
        var startIndex = (page - 1) * pageSize;
        var it = dailyItems.iterator();
        ShopItem shopItem;
        for (int i = 0, slotIndex = 0; it.hasNext() && i < (page * pageSize); i++) {
            shopItem = it.next();
            if (i >= startIndex) {
                inventory.addItem(itemMapper.toItemStack(shopItem));
                slotsMap.put(slotIndex++, shopItem);
            }
        }
        return inventory;
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(this.shopInventory)) {
            return;
        }
        event.setCancelled(true);
        var slotClicked = event.getRawSlot();
        if (slotClicked == PREVIOUS_PAGE_SLOT && page > 1) {
            closeInventory();
            manager.open(player, page - 1);
        } else if (slotClicked == NEXT_PAGE_SLOT && page < totalPages) {
            closeInventory();
            manager.open(player, page + 1);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            HandlerList.unregisterAll(this);
            manager.close(this);
        }
    }
}
