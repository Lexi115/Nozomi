package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.ProjectNozomi;
import io.github.lexi115.projectNozomi.shop.ShopService;
import io.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class ShopCommands {

    private final ProjectNozomi plugin;

    @Dependency
    private ShopService shopService;

    @Dependency
    private ShopGuiManager shopGuiManager;

    public ShopCommands(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final Player sender, @Default("1") @Range(min = 1) final Integer page) {
        sender.sendMessage("This is daily");
        var dailyItems = shopService.getDailyItems();
        dailyItems.forEach(item -> sender.sendMessage(item.getName()));
        shopGuiManager.open(sender, page);
    }

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final BukkitCommandActor sender) {
        shopGuiManager.closeAll();
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig();
    }
}
