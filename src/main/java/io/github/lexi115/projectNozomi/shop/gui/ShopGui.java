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

    private final ShopGuiManager manager;

    public ShopGui(
            final JavaPlugin plugin,
            final ShopService shopService,
            final ItemMapper itemMapper,
            final Player player,
            final ShopGuiManager manager
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.player = player;
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
        int slotIndex = 0;
        for (var dailyItem : dailyItems) {
            inventory.addItem(itemMapper.toItemStack(dailyItem));
            slotsMap.put(slotIndex++, dailyItem);
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
        // todo ricordarsi di gestire caso in cui si clicchi slot vuoto (o fuori) --> eccezione out of bounds
        event.setCancelled(true);
        plugin.getLogger().info("clicked on " + event.getRawSlot());
        plugin.getLogger().info("shopitem: " + slotsMap.get(event.getRawSlot()).getName());
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            HandlerList.unregisterAll(this);
            manager.close(this);
        }
    }
}
