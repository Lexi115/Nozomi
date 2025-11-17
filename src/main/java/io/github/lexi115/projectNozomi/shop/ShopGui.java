package io.github.lexi115.projectNozomi.shop;

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

    private final ItemMapper mapper;

    private final Map<Integer, ShopItem> slotsMap = new HashMap<>();

    public ShopGui(final JavaPlugin plugin, final ShopService shopService, final ItemMapper mapper) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.mapper = mapper;
        shopInventory = createShopInventory();
        registerEvents();
    }

    public void openInventory(final Player player) {
        player.openInventory(shopInventory);
    }

    public void registerEvents() {
        var pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(this, plugin);
        plugin.getLogger().info("registered event");
    }

    private Inventory createShopInventory() {
        var inventory = Bukkit.createInventory(null, 54, "Yo");
        var dailyItems = shopService.getDailyItems();
        int slotIndex = 0;
        for (var dailyItem : dailyItems) {
            inventory.addItem(mapper.toItemStack(dailyItem));
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
        event.setCancelled(true);
        plugin.getLogger().info("clicked on " + event.getRawSlot());
        plugin.getLogger().info("shopitem: " + slotsMap.get(event.getRawSlot()).getName());
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            HandlerList.unregisterAll(this);
            plugin.getLogger().info("unregistered event");
        }
    }
}
