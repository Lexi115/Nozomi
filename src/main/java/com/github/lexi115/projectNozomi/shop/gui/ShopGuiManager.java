package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.github.lexi115.projectNozomi.shop.ItemMapper;
import com.github.lexi115.projectNozomi.shop.ShopExceptionHandler;
import com.github.lexi115.projectNozomi.shop.ShopNotFoundException;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that manages the lifecycle of {@link ShopGui} objects, from creating / opening them to closing one
 * or all of them.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class ShopGuiManager {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The shop service.
     */
    private final ShopService shopService;

    /**
     * The item mapper.
     */
    private final ItemMapper itemMapper;

    /**
     * Utility class to send formatted messages to a user.
     */
    private final MessageUtils messageUtils;

    /**
     * Utility class for recurring string operations.
     */
    private final StringUtils stringUtils;

    /**
     * Handler for shop-related exceptions.
     */
    private final ShopExceptionHandler shopExceptionHandler;

    /**
     * Set containing all currently open (and managed) GUIs.
     */
    private final Set<ShopGui> openGuis = new HashSet<>();

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param shopService The shop service.
     * @param itemMapper The item mapper.
     * @param messageUtils The message utility class.
     * @param stringUtils The string utility class.
     * @param shopExceptionHandler The handler for shop-related exceptions.
     * @since 1.0
     */
    @Inject
    public ShopGuiManager(
            final ProjectNozomi plugin,
            final ShopService shopService,
            final ItemMapper itemMapper,
            final MessageUtils messageUtils,
            final StringUtils stringUtils,
            final ShopExceptionHandler shopExceptionHandler
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.messageUtils = messageUtils;
        this.stringUtils = stringUtils;
        this.shopExceptionHandler = shopExceptionHandler;
    }

    /**
     * Creates and opens a new {@link ShopGui} at the specified page for a certain player.
     * @param player The target player.
     * @param page The page number.
     * @throws InvalidPageException If the provided page number is not greater
     *                              than 0 or exceeds the total number of pages.
     * @since 1.0
     */
    public void open(final Player player, final int page) {
        var shopGui = new ShopGui(plugin, shopService, itemMapper, stringUtils, messageUtils, shopExceptionHandler,
                player, page, this, getGuiDetails());
        shopGui.open();
        openGuis.add(shopGui);
    }

    /**
     * Closes the specified {@link ShopGui}.
     *
     * @param gui The GUI to close.
     * @since 1.0
     */
    public void close(final ShopGui gui) {
        openGuis.remove(gui);
    }

    /**
     * Closes all currently open {@link ShopGui} GUIs.
     *
     * @since 1.0
     */
    public void closeAll() {
        openGuis.forEach(ShopGui::close);
    }

    private ShopGuiDetails getGuiDetails() {
        var section = plugin.getShopConfig().getConfigurationSection("shop.gui");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        int pageRows = section.getInt("rows", 5);
        int guiSize = (pageRows + 1) * 9;
        if (guiSize < 18 || guiSize > 54) {
            throw new InvalidGuiSizeException();
        }
        var itemSlots = (new LinkedHashSet<>(section.getIntegerList("item-slots"))).toArray(new Integer[0]);
        var lastAvailableSlot = guiSize - 10;
        var pageSize = itemSlots.length == 0
                ? lastAvailableSlot + 1 : Math.min(lastAvailableSlot + 1, itemSlots.length);
        return generateGuiDetails(section, guiSize, pageSize, lastAvailableSlot, itemSlots);
    }

    private ShopGuiDetails generateGuiDetails(
            final @NonNull ConfigurationSection section,
            final int guiSize,
            final int pageSize,
            final int lastAvailableSlot,
            final Integer[] itemSlots) {
        return ShopGuiDetails.builder()
                .title(stringUtils.colorize(section.getString("title", "?")))
                .guiSize(guiSize)
                .pageSize(pageSize)
                .lastAvailableSlot(lastAvailableSlot)
                .itemSlots(itemSlots)
                .previousPage(createGuiElement(
                        section, "navigation.previous-page", guiSize - 6, Material.ARROW))
                .nextPage(createGuiElement(
                        section, "navigation.next-page", guiSize - 4, Material.ARROW))
                .currentPage(createGuiElement(
                        section, "navigation.current-page", guiSize - 5, Material.COMPASS))
                .build();
    }

    private GuiElement createGuiElement(
            final @NonNull ConfigurationSection section,
            final String path,
            final int slot,
            final @NonNull Material defaultMaterial
    ) {
        return GuiElement.builder()
                .name(section.getString(path + ".name", "&r"))
                .lore(section.getStringList(path + ".lore"))
                .material(Material.matchMaterial(section.getString(path + ".material", defaultMaterial.name())))
                .slot(slot)
                .build();
    }
}
