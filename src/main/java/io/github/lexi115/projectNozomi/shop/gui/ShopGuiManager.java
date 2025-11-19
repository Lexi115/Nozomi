package io.github.lexi115.projectNozomi.shop.gui;

import io.github.lexi115.projectNozomi.shop.ShopService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class ShopGuiManager {

    private final JavaPlugin plugin;

    private final ShopService shopService;

    private final ItemMapper itemMapper;

    private final Set<ShopGui> openGuis = new HashSet<>();

    public ShopGuiManager(final JavaPlugin plugin, final ShopService shopService, final ItemMapper itemMapper) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.itemMapper = itemMapper;
    }

    public void open(final Player player, final Integer page) {
        var shopGui = new ShopGui(plugin, shopService, itemMapper, player, page, this);
        shopGui.openInventory();
        openGuis.add(shopGui);
    }

    public void closeAll() {
        openGuis.forEach(ShopGui::closeInventory);
    }

    public void close(final ShopGui gui) {
        openGuis.remove(gui);
    }
}
