package io.github.lexi115.projectNozomi.commands;

import io.github.lexi115.projectNozomi.ProjectNozomi;
import org.slf4j.Logger;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class PluginCommands {

    @Dependency
    private Logger log;

    private final ProjectNozomi plugin;

    public PluginCommands(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    @Subcommand("info")
    @CommandPermission("nozomi.info")
    public void info(final BukkitCommandActor sender) {
        log.info(plugin.getMessagesConfig().getString("info"));
        sender.reply("Project Nozomi by Lexi115");
    }

    @Subcommand("reload")
    @CommandPermission("nozomi.reload")
    public void reload(final BukkitCommandActor sender) {
        plugin.reloadConfigs();
        sender.reply("Reloaded plugin!");
    }
}
