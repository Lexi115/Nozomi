package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.shop.ItemMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.ShopNotFoundException;
import com.github.lexi115.projectNozomi.shop.ShopService;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Singleton
public class ShopGuiManager {

    private final ProjectNozomi plugin;

    private final ShopService shopService;

    private final ItemMapper itemMapper;

    private final Set<ShopGui> openGuis = new HashSet<>();

    @Inject
    public ShopGuiManager(final ProjectNozomi plugin, final ShopService shopService, final ItemMapper itemMapper) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
    }

    public void open(final Player player, final int page) {
        var shopGui = new ShopGui(plugin, shopService, itemMapper, player, page, this, getGuiDetails());
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
                .title(section.getString("title", "?"))
                .guiSize(guiSize)
                .pageSize(pageSize)
                .lastAvailableSlot(lastAvailableSlot)
                .itemSlots(itemSlots)
                .previousPage(GuiElement.builder()
                        .name(section.getString("navigation.previous-page.name", "Previous Page"))
                        .material(Material.matchMaterial(
                                section.getString("navigation.previous-page.material", Material.ARROW.name())))
                        .slot(guiSize - 6)
                        .build())
                .nextPage(GuiElement.builder()
                        .name(section.getString("navigation.next-page.name", "Next Page"))
                        .material(Material.matchMaterial(
                                section.getString("navigation.next-page.material", Material.ARROW.name())))
                        .slot(guiSize - 4)
                        .build())
                .currentPage(GuiElement.builder()
                        .name(section.getString("navigation.current-page.name",
                                "Page %page% of %totalPages%"))
                        .material(Material.matchMaterial(
                                section.getString("navigation.current-page.material", Material.COMPASS.name())))
                        .slot(guiSize - 5)
                        .build())
                .build();
    }
}
