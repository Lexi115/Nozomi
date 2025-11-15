package io.github.lexi115.projectNozomi.commands;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
@AllArgsConstructor
public class ShopCommands {

    private Logger log;

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final BukkitCommandActor sender) {
        sender.reply("This is daily");
        log.info("DAILY EXECUTED!");
    }
}
