package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.shop.Shop;
import io.github.lexi115.projectNozomi.shop.ShopService;
import org.slf4j.Logger;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class ShopCommands {

    @Dependency
    private Logger log;

    @Dependency
    private ShopService shopService;

    @Dependency
    private Shop shop;

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final BukkitCommandActor sender) {
        sender.reply("This is daily");
        var dailyItems = shop.getDailyItems();
        dailyItems.forEach(item -> sender.reply(item.getName()));
        log.info("DAILY EXECUTED!");
    }

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final BukkitCommandActor sender) {
        shopService.refreshDailyItems(shop);
        log.info("REFRESHED DAILY");
    }
}
