package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.ProjectNozomi;
import lombok.AllArgsConstructor;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
@AllArgsConstructor
public class ShopCommands {

    private ProjectNozomi plugin;

    @Subcommand("daily")
    @CommandPermission("nozomi.daily")
    public void daily(final BukkitCommandActor sender) {
        sender.reply("This is daily");
        sender.reply(plugin.getMessagesConfig().getString("info"));
    }
}
