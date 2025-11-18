package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.ProjectNozomi;
import io.github.lexi115.projectNozomi.shop.ItemMapper;
import io.github.lexi115.projectNozomi.shop.ShopGui;
import io.github.lexi115.projectNozomi.shop.ShopService;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class ShopCommands {

    private final ProjectNozomi plugin;

    @Dependency
    private ShopService shopService;

    public ShopCommands(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final Player sender) {
        sender.sendMessage("This is daily");
        var dailyItems = shopService.getDailyItems();
        dailyItems.forEach(item -> sender.sendMessage(item.getName()));
        var shopGui = new ShopGui(plugin, shopService, new ItemMapper());
        shopGui.openInventory(sender);
    }

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final BukkitCommandActor sender) {
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig(plugin.getDailyItemsConfig());
    }
}
