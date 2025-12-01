package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.github.lexi115.projectNozomi.shop.*;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class that represents a shop GUI inventory, where players can actually interact with the shop and sell items by
 * clicking on their respective slots. It supports pagination by including UI elements at the bottom to change page.
 *
 * @author Lexi115
 * @since 1.0
 */
public class ShopGui implements Listener {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * Service class that handles all shop operations.
     */
    private final ShopService shopService;

    /**
     * The item mapper class.
     */
    private final ItemMapper itemMapper;

    /**
     * Utility class for recurring string operations.
     */
    private final StringUtils stringUtils;

    /**
     * Utility class to send formatted messages to a user.
     */
    private final MessageUtils messageUtils;

    /**
     * Handler for shop-related exceptions.
     */
    private final ShopExceptionHandler shopExceptionHandler;

    /**
     * The shop GUI inventory.
     */
    private final Inventory shopInventory;

    /**
     * A map that assigns an inventory slot to each shop item. It's used to retrieve the original {@link ShopItem}
     * object when a user clicks on the related {@link org.bukkit.inventory.ItemStack} in the shop inventory and
     * proceed with the selling operation.
     */
    private final Map<Integer, ShopItem> slotsMap = new HashMap<>();

    /**
     * The player that is using the GUI.
     */
    private final Player player;

    /**
     * The shop page number.
     */
    private final int page;

    /**
     * The total number of pages.
     */
    private final int totalPages;

    /**
     * The shop GUI manager.
     */
    private final ShopGuiManager guiManager;

    /**
     * Information about the shop GUI (name, slots, UI elements).
     */
    private final ShopGuiDetails guiDetails;

    /**
     * Previous page UI element.
     */
    private final GuiElement previousPageElement;

    /**
     * Next page UI element.
     */
    private final GuiElement nextPageElement;

    /**
     * Current page UI element.
     */
    private final GuiElement currentPageElement;

    /**
     * The current daily items in the shop.
     */
    private final Collection<ShopItem> dailyItems;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param shopService The shop service.
     * @param itemMapper The item mapper.
     * @param stringUtils The string utility class.
     * @param messageUtils The message utility class.
     * @param shopExceptionHandler The handler for shop-related exceptions.
     * @param player The player using the shop GUI.
     * @param page The page number.
     * @param guiManager The shop GUI manager.
     * @param guiDetails The GUI information.
     * @throws InvalidPageException If the provided page number is not greater
     *                              than 0 or exceeds the total number of pages.
     * @since 1.0
     */
    public ShopGui(
            final ProjectNozomi plugin,
            final @NonNull ShopService shopService,
            final ItemMapper itemMapper,
            final StringUtils stringUtils,
            final MessageUtils messageUtils,
            final ShopExceptionHandler shopExceptionHandler,
            final Player player,
            final int page,
            final ShopGuiManager guiManager,
            final @NonNull ShopGuiDetails guiDetails
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.stringUtils = stringUtils;
        this.messageUtils = messageUtils;
        this.shopExceptionHandler = shopExceptionHandler;
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
            throw new InvalidPageException(page, totalPages);
        }
        this.shopInventory = createShopInventory();
        registerEvents();
    }

    /**
     * Opens the shop inventory for the previously specified player.
     *
     * @since 1.0
     */
    public void open() {
        player.openInventory(shopInventory);
    }

    /**
     * Closes the shop inventory for the previously specified player. It is mainly used by the {@link ShopGuiManager}
     * to close all open shop inventories before daily items are refreshed or the plugin is reloaded.
     *
     * @see ShopGuiManager
     * @since 1.0
     */
    public void close() {
        player.closeInventory();
    }

    /**
     * Gets invoked when a player clicks on any slot in the shop inventory. If the slot is mapped to a {@link ShopItem}
     * in the <code>slotsMap</code> map, check if the latter can be sold. If it's a navigation button
     * (previous / next page element), try opening another shop GUI on the requested page. If the slot is empty,
     * cancel the event.
     *
     * @param event The event to handle.
     * @since 1.0
     */
    @EventHandler
    public void onInventoryClick(final @NonNull InventoryClickEvent event) {
        if (!event.getInventory().equals(this.shopInventory)) {
            return;
        }
        event.setCancelled(true);
        if (event.getClick() == ClickType.LEFT) {
            var player = (Player) event.getWhoClicked();
            var slotClicked = event.getRawSlot();
            var clickedShopItem = slotsMap.get(slotClicked);
            if (clickedShopItem == null) {
                checkForNavigationButtonClick(event.getRawSlot());
                return;
            }
            sellItem(player, clickedShopItem);
        }
    }

    /**
     * Gets invoked when a player tries dragging an item around the shop inventory. Since such behavior is not allowed,
     * the event is automatically cancelled.
     *
     * @param event The event to handle.
     * @since 1.0
     */
    @EventHandler
    public void onInventoryDrag(final @NonNull InventoryDragEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            event.setCancelled(true);
        }
    }

    /**
     * Gets invoked when a player closes the shop inventory, either voluntarily or because the daily items were
     * refreshed or the plugin was reloaded. The listener related to this GUI is automatically unregistered.
     *
     * @param event The event to handle.
     * @since 1.0
     */
    @EventHandler
    public void onInventoryClose(final @NonNull InventoryCloseEvent event) {
        if (event.getInventory().equals(this.shopInventory)) {
            HandlerList.unregisterAll(this);
            guiManager.close(this);
        }
    }

    private @NonNull Inventory createShopInventory() {
        var inventory = Bukkit.createInventory(null, guiDetails.getGuiSize(), guiDetails.getTitle());
        putShopItems(inventory, dailyItems);
        putUiElements(inventory);
        return inventory;
    }

    private void putShopItems(
            final @NonNull Inventory inventory,
            final @NonNull Collection<ShopItem> items
    ) {
        var lastAvailableSlot = guiDetails.getLastAvailableSlot();
        var pageSize = guiDetails.getPageSize();
        var itemSlots = guiDetails.getItemSlots();
        var slotIndex = itemSlots.length == 0 ? -1 : itemSlots[0];
        var itemsList = items.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .toList();
        var iterator = itemsList.iterator();
        for (int i = 0; iterator.hasNext() && i < (page * pageSize); i++) {
            var currentItem = iterator.next();
            if (currentItem.getMaterial() == Material.AIR) {
                continue;
            }
            slotIndex = itemSlots.length == 0 ? slotIndex + 1 : itemSlots[i];
            if (slotIndex >= 0 && slotIndex <= lastAvailableSlot) {
                putShopItem(inventory, currentItem, slotIndex);
            }
        }
    }

    private void putShopItem(
            final @NotNull Inventory inventory,
            final @NonNull ShopItem item,
            final int slotIndex
    ) {
        var friendlyMaterialName = stringUtils.toUserFriendly(item.getMaterial().toString());
        var placeholders = new PlaceholderMap()
                .set("name", Optional.ofNullable(item.getName()).orElse(friendlyMaterialName))
                .set("material", friendlyMaterialName)
                .set("amount", item.getAmount());
        inventory.setItem(slotIndex, itemMapper.toItemStack(item, 1, placeholders.map()));
        slotsMap.put(slotIndex, item);
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
        var placeholders = new PlaceholderMap()
                .set("page", page)
                .set("totalPages", totalPages);
        inventory.setItem(currentPageElement.getSlot(),
                itemMapper.toItemStack(currentPageElement, 1, placeholders.map()));
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

    private void sellItem(final @NonNull Player player, final @NonNull ShopItem item) {
        var materialName = stringUtils.toUserFriendly(item.getMaterial().toString());
        var placeholders = new PlaceholderMap()
                .set("name", Optional.ofNullable(item.getName()).orElse(materialName))
                .set("material", materialName)
                .set("amount", item.getAmount());
        try {
            shopService.sellItem(player, item);
            player.sendMessage(messageUtils.getPrefix() + messageUtils.get("info.item-sold", placeholders.map()));
        } catch (NotEnoughItemsException e) {
            shopExceptionHandler.onNotEnoughItems(e, player);
        } catch (NoUsesException e) {
            shopExceptionHandler.onNoUses(e, player);
        }
    }

    private void registerEvents() {
        var pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(this, plugin);
    }
}
