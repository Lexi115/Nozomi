package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.shop.ShopService;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class ShopCommands {

    @Dependency
    private ShopService shopService;

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final BukkitCommandActor sender) {
        sender.reply("This is daily");
        var dailyItems = shopService.getDailyItems();
        dailyItems.forEach(item -> sender.reply(item.getName()));
    }

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final BukkitCommandActor sender) {
        shopService.refreshDailyItems();
    }
}
