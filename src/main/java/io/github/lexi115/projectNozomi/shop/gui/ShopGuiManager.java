package io.github.lexi115.projectNozomi.shop.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.lexi115.projectNozomi.ProjectNozomi;
import io.github.lexi115.projectNozomi.shop.ShopNotFoundException;
import io.github.lexi115.projectNozomi.shop.ShopService;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
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

    public void open(final Player player, final Integer page) {
        var shopGui = new ShopGui(plugin, shopService, itemMapper, player, page, this, getGuiDetails());
        shopGui.openInventory();
        openGuis.add(shopGui);
    }

    public void close(final ShopGui gui) {
        openGuis.remove(gui);
    }

    public void closeAll() {
        openGuis.forEach(ShopGui::closeInventory);
    }

    private ShopGuiDetails getGuiDetails() {
        var section = plugin.getShopConfig().getConfigurationSection("shop.gui");
        if (section == null) {
            throw new ShopNotFoundException();
        }
        // GUI size must be a multiple of 9
        int guiSize = section.getInt("size", 54);
        if (guiSize < 18 || guiSize > 54 || guiSize % 9 != 0) {
            throw new InvalidGuiSizeException();
        }
        return ShopGuiDetails.builder()
                .title(section.getString("title", "?"))
                .size(guiSize)
                .itemSlots(new LinkedHashSet<>(section.getIntegerList("item-slots")))
                .previousPage(GuiElement.builder()
                        .name(section.getString("navigation.previous-page.name", "Previous Page"))
                        .material(Material.matchMaterial(
                                section.getString("navigation.previous-page.material", Material.ARROW.name())))
                        .slot(guiSize - 9)
                        .build())
                .nextPage(GuiElement.builder()
                        .name(section.getString("navigation.next-page.name", "Next Page"))
                        .material(Material.matchMaterial(
                                section.getString("navigation.next-page.material", Material.ARROW.name())))
                        .slot(guiSize - 1)
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
