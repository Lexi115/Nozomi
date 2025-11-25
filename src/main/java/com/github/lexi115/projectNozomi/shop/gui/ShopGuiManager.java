package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.github.lexi115.projectNozomi.shop.ItemMapper;
import com.github.lexi115.projectNozomi.shop.ShopNotFoundException;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Singleton
public class ShopGuiManager {

    private final ProjectNozomi plugin;

    private final ShopService shopService;

    private final ItemMapper itemMapper;

    private final MessageUtils messageUtils;

    private final StringUtils stringUtils;

    private final Set<ShopGui> openGuis = new HashSet<>();

    @Inject
    public ShopGuiManager(
            final ProjectNozomi plugin,
            final ShopService shopService,
            final ItemMapper itemMapper,
            final MessageUtils messageUtils,
            final StringUtils stringUtils
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
        this.messageUtils = messageUtils;
        this.stringUtils = stringUtils;
    }

    public void open(final Player player, final int page) {
        var shopGui = new ShopGui(plugin, shopService, itemMapper, messageUtils,
                player, page, this, getGuiDetails());
        shopGui.open();
        openGuis.add(shopGui);
    }

    public void close(final ShopGui gui) {
        openGuis.remove(gui);
    }

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
            final ConfigurationSection section,
            final String path,
            final int slot,
            final Material defaultMaterial
    ) {
        return GuiElement.builder()
                .name(stringUtils.colorize(section.getString(path + ".name", "")))
                .material(Material.matchMaterial(
                        section.getString(path + ".material", defaultMaterial.name())))
                .lore(section.getStringList(path + ".lore"))
                .slot(slot)
                .build();
    }
}
