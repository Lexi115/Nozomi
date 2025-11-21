package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.ShopItem;
import com.github.lexi115.projectNozomi.shop.ShopService;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShopGui implements Listener {

    private final ProjectNozomi plugin;

    private final Inventory shopInventory;

    private final ShopService shopService;

    private final ItemMapper itemMapper;

    private final Map<Integer, ShopItem> slotsMap = new HashMap<>();

    private final Player player;

    private final int page;

    private final int totalPages;

    private final ShopGuiManager guiManager;

    private final ShopGuiDetails guiDetails;

    private final GuiElement previousPageElement;

    private final GuiElement nextPageElement;

    private final GuiElement currentPageElement;

    private final Collection<ShopItem> dailyItems;

    public ShopGui(
            final ProjectNozomi plugin,
            final @NonNull ShopService shopService,
            final ItemMapper itemMapper,
            final Player player,
            final int page,
            final ShopGuiManager guiManager,
            final @NonNull ShopGuiDetails guiDetails
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.player = player;
        this.page = page;
        this.guiManager = guiManager;
        this.guiDetails = guiDetails;
        this.previousPageElement = guiDetails.getPreviousPage();
        this.nextPageElement = guiDetails.getNextPage();
        this.currentPageElement = guiDetails.getCurrentPage();
        this.dailyItems = shopService.getDailyItems();
        this.totalPages = (int) Math.ceil(dailyItems.size() / (double) guiDetails.getPageSize());
        if (page < 1 || page > totalPages) {
            throw new InvalidPageException();
        }
        this.shopInventory = createShopInventory();
        registerEvents();
    }

    @EventHandler
    public void onInventoryClick(final @NonNull InventoryClickEvent event) {
        if (!event.getInventory().equals(this.shopInventory)) {
            return;
        }
        event.setCancelled(true);
        var player = (Player) event.getWhoClicked();
        var slotClicked = event.getRawSlot();
        var clickedShopItem = slotsMap.get(slotClicked);
        if (clickedShopItem != null) {
            if (shopService.sellItem(player, clickedShopItem)) {
                System.out.println("sold item");
            } else {
                System.out.println("item not sold");
            }
        } else {
            checkForNavigationButtonClick(event.getRawSlot());
        }
    }

    @EventHandler
    public void onInventoryDrag(final @NonNull InventoryDragEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final @NonNull InventoryCloseEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            HandlerList.unregisterAll(this);
            guiManager.close(this);
        }
    }

    public void open() {
        player.openInventory(shopInventory);
    }

    public void close() {
        player.closeInventory();
    }

    private @NonNull Inventory createShopInventory() {
        var inventory = Bukkit.createInventory(null, guiDetails.getGuiSize(), guiDetails.getTitle());
        putDailyItems(inventory, dailyItems);
        putUiElements(inventory);
        return inventory;
    }

    private void putDailyItems(
            final @NonNull Inventory inventory,
            final @NonNull Collection<ShopItem> dailyItems
    ) {
        var lastAvailableSlot = guiDetails.getLastAvailableSlot();
        var pageSize = guiDetails.getPageSize();
        var itemSlots = guiDetails.getItemSlots();
        var slotIndex = itemSlots.length == 0 ? -1 : itemSlots[0];
        var dailyItemsList = dailyItems.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();
        ShopItem currentItem;
        var iterator = dailyItemsList.iterator();
        for (int i = 0; iterator.hasNext() && i < (page * pageSize); i++) {
            currentItem = iterator.next();
            slotIndex = itemSlots.length == 0 ? slotIndex + 1 : itemSlots[i];
            if (slotIndex >= 0 && slotIndex <= lastAvailableSlot) {
                inventory.setItem(slotIndex, itemMapper.toItemStack(currentItem));
                slotsMap.put(slotIndex, currentItem);
            }
        }
    }

    private void putUiElements(final @NonNull Inventory inventory) {
        // Previous page
        if (page > 1) {
            inventory.setItem(previousPageElement.getSlot(), itemMapper.toItemStack(previousPageElement));
        }
        // Next page
        if (page < totalPages) {
            inventory.setItem(nextPageElement.getSlot(), itemMapper.toItemStack(nextPageElement));
        }
        // Current page
        var placeholders = new HashMap<String, String>();
        placeholders.put("page", String.valueOf(page));
        placeholders.put("totalPages", String.valueOf(totalPages));
        inventory.setItem(currentPageElement.getSlot(),
                itemMapper.toItemStack(currentPageElement, 1, placeholders));
    }

    private void checkForNavigationButtonClick(final int slotClicked) {
        if (slotClicked == previousPageElement.getSlot() && page > 1) {
            close();
            guiManager.open(player, page - 1);
        } else if (slotClicked == nextPageElement.getSlot() && page < totalPages) {
            close();
            guiManager.open(player, page + 1);
        }
    }

    private void registerEvents() {
        var pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }
}
