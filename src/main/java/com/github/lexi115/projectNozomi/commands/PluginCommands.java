package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.Help;

/**
 * Plugin-related commands.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
@Command({"noz"})
public class PluginCommands implements Commands {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The string utility class.
     */
    private final StringUtils stringUtils;

    /**
     * The message utility class.
     */
    private final MessageUtils messageUtils;

    /**
     * The config containing useful constant values used throughout the project.
     */
    private final FileConfiguration constantsConfig;

    /**
     * Constructor.
     *
     * @param plugin       The plugin instance.
     * @param stringUtils  The string utility class.
     * @param messageUtils The message utility class.
     * @since 1.0
     */
    @Inject
    public PluginCommands(
            final @NonNull ProjectNozomi plugin,
            final StringUtils stringUtils,
            final MessageUtils messageUtils
    ) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
        this.messageUtils = messageUtils;
        this.constantsConfig = plugin.getConstantsConfig();
    }

    /**
     * Sends back information about the plugin, like the name, version and the author.
     *
     * @param sender The command sender.
     * @since 1.0
     */
    @Subcommand("info")
    @CommandPermission("nozomi.info")
    public void info(final @NonNull BukkitCommandActor sender) {
        var pluginDescription = plugin.getDescription();
        var placeholders = new PlaceholderMap()
                .set("name", pluginDescription.getName())
                .set("version", pluginDescription.getVersion())
                .set("author", pluginDescription.getAuthors().getFirst());
        var infoMessage = constantsConfig.getString("info.command-message", "");
        sender.reply(stringUtils.format(infoMessage, placeholders.map()));
    }

    /**
     * Reloads the plugin and its configs.
     *
     * @param sender The command sender.
     * @since 1.0
     */
    @Subcommand("reload")
    @CommandPermission("nozomi.reload")
    public void reload(final @NonNull BukkitCommandActor sender) {
        plugin.reloadPlugin();
        sender.reply(messageUtils.getPrefix() + messageUtils.get("info.reloaded"));
    }

    /**
     * Sends back a help manual containing every registered command for this plugin and its description.
     *
     * @param sender The command sender.
     * @param page The manual page.
     * @param commands The related commands.
     * @since 1.0
     */
    @Subcommand("help")
    @CommandPermission("nozomi.help")
    public void help(
            final @NonNull BukkitCommandActor sender,
            final @Range(min = 1) @Default("1") int page,
            final @NonNull Help.RelatedCommands<BukkitCommandActor> commands
    ) {
        var commandsPerPage = constantsConfig.getInt("help.commands-per-page");
        var list = commands.paginate(page, commandsPerPage);
        var placeholders = new PlaceholderMap();
        var sb = new StringBuilder();
        // Header
        sb.append(stringUtils.colorize(messageUtils.get("help.header"))).append('\n');
        // Entries
        list.forEach(command -> {
            var usage = command.usage();
            var splitUsage = usage.split(" ");
            var description = messageUtils.get("help.descriptions." + splitUsage[splitUsage.length > 1 ? 1 : 0]);
            placeholders
                    .set("command", usage)
                    .set("description", description);
            sb.append(stringUtils.format(messageUtils.get("help.entry"), placeholders.map())).append('\n');
        });
        // Footer
        placeholders
                .clear()
                .set("page", page)
                .set("totalPages", commands.numberOfPages(commandsPerPage));
        sb.append(stringUtils.colorize(messageUtils.get("help.footer", placeholders.map())));
        sender.reply(sb.toString());
    }
}
