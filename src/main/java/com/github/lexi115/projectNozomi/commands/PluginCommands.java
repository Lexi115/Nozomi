package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.google.inject.Inject;
import com.github.lexi115.projectNozomi.ProjectNozomi;
import lombok.NonNull;
import org.slf4j.Logger;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class PluginCommands {

    private final ProjectNozomi plugin;

    private final Logger log;

    private final MessageUtils messageUtils;

    @Inject
    public PluginCommands(final ProjectNozomi plugin, final Logger log, final MessageUtils messageUtils) {
        this.plugin = plugin;
        this.log = log;
        this.messageUtils = messageUtils;
    }

    @Subcommand("info")
    @CommandPermission("nozomi.info")
    public void info(final @NonNull BukkitCommandActor sender) {
        log.info(plugin.getMessagesConfig().getString("info"));
        sender.reply("Project Nozomi by Lexi115");
        sender.reply(messageUtils.get("info"));
    }

    @Subcommand("reload")
    @CommandPermission("nozomi.reload")
    public void reload(final @NonNull BukkitCommandActor sender) {
        plugin.reloadPlugin();
        sender.reply("Reloaded plugin!");
    }
}
