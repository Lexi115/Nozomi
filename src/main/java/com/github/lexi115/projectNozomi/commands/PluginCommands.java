package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.ext.VaultExtension;
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

    private final VaultExtension vault;

    private final Logger log;

    @Inject
    public PluginCommands(final ProjectNozomi plugin, final Logger log) {
        this.plugin = plugin;
        this.vault = plugin.getVault();
        this.log = log;
    }

    @Subcommand("info")
    @CommandPermission("nozomi.info")
    public void info(final @NonNull BukkitCommandActor sender) {
        log.info(plugin.getMessagesConfig().getString("info"));
        log.info(vault.isEnabled() ? "vault siiii" : "vault noooo");
        sender.reply("Project Nozomi by Lexi115");
    }

    @Subcommand("reload")
    @CommandPermission("nozomi.reload")
    public void reload(final @NonNull BukkitCommandActor sender) {
        plugin.reloadPlugin();
        sender.reply("Reloaded plugin!");
    }
}
