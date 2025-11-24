package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.Help;

@Singleton
@Command({"noz"})
public class PluginCommands implements Commands {

    private final ProjectNozomi plugin;

    private final StringUtils stringUtils;

    private final MessageUtils messageUtils;

    @Inject
    public PluginCommands(final ProjectNozomi plugin, final StringUtils stringUtils, final MessageUtils messageUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
        this.messageUtils = messageUtils;
    }

    @Subcommand("info")
    @CommandPermission("nozomi.info")
    public void info(final @NonNull BukkitCommandActor sender) {
        var pluginDescription = plugin.getDescription();
        var infoMessage = String.format("&a%s v%s &7by &e%s &c❤️",
                pluginDescription.getName(), pluginDescription.getVersion(), pluginDescription.getAuthors().getFirst());
        sender.reply(stringUtils.colorize(infoMessage));
    }

    @Subcommand("reload")
    @CommandPermission("nozomi.reload")
    public void reload(final @NonNull BukkitCommandActor sender) {
        plugin.reloadPlugin();
        sender.reply(messageUtils.getPrefix() + messageUtils.get("info.reloaded"));
    }

    @Subcommand("help")
    @CommandPermission("nozomi.help")
    public void help(
            final @NonNull BukkitCommandActor sender,
            final @Range(min = 1) @Default("1") int page,
            final Help.RelatedCommands<BukkitCommandActor> commands
    ) {
        var list = commands.paginate(page, 10);
        var placeholders = new PlaceholderMap();
        var sb = new StringBuilder();
        sb.append(stringUtils.colorize(messageUtils.get("help.header"))).append('\n');
        String usage;
        String[] splitUsage;
        String description;
        for (var command : list) {
            usage = command.usage();
            splitUsage = usage.split(" ");
            description = messageUtils.get("help.descriptions." + splitUsage[splitUsage.length > 1 ? 1 : 0]);
            placeholders.set("command", usage);
            placeholders.set("description", description);
            sb.append(stringUtils.format(messageUtils.get("help.entry"), placeholders.map())).append('\n');
        }
        placeholders.clear();
        placeholders
                .set("page", page)
                .set("totalPages", commands.numberOfPages(10));
        sb.append(stringUtils.colorize(messageUtils.get("help.footer", placeholders.map())));
        sender.reply(sb.toString());
    }
}
