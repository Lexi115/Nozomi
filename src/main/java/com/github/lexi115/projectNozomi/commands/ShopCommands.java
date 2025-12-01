package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.github.lexi115.projectNozomi.database.services.ShopUsesService;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * Shop-related commands.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
@Command({"noz"})
public class ShopCommands implements Commands {

    /**
     * The plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * The shop service.
     */
    private final ShopService shopService;

    /**
     * The shop uses service.
     */
    private final ShopUsesService shopUsesService;

    /**
     * The shop GUI manager.
     */
    private final ShopGuiManager shopGuiManager;

    /**
     * The message utility class.
     */
    private final MessageUtils messageUtils;

    /**
     * The commands utility class.
     */
    private final CommandUtils commandUtils;

    /**
     * The command exception handler.
     */
    private final CommandExceptionHandler commandExceptionHandler;

    /**
     * Constructor.
     *
     * @param plugin                  The plugin instance.
     * @param shopService             The shop service.
     * @param shopUsesService         The shop uses service.
     * @param shopGuiManager          The shop GUI manager.
     * @param messageUtils            The message utility class.
     * @param commandUtils            The commands utility class.
     * @param commandExceptionHandler The command exception handler.
     * @since 1.0
     */
    @Inject
    public ShopCommands(
            final JavaPlugin plugin,
            final ShopService shopService,
            final ShopUsesService shopUsesService,
            final ShopGuiManager shopGuiManager,
            final MessageUtils messageUtils,
            final CommandUtils commandUtils,
            final CommandExceptionHandler commandExceptionHandler
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.shopUsesService = shopUsesService;
        this.shopGuiManager = shopGuiManager;
        this.messageUtils = messageUtils;
        this.commandUtils = commandUtils;
        this.commandExceptionHandler = commandExceptionHandler;
    }

    /**
     * Opens the shop GUI for a certain player.
     *
     * @param sender The command sender.
     * @param target The target player.
     * @param page The shop page.
     * @since 1.0
     */
    @Subcommand("shop <target> <page>")
    @CommandPermission("nozomi.shop")
    public void shop(
            final @NonNull BukkitCommandActor sender,
            final @Default("me") @NonNull Player target,
            @Default("1") @Range(min = 1) final int page
    ) {
        if (!commandUtils.isSenderTarget(sender, target)
                && !commandUtils.hasPermission(sender, "nozomi.shop.others")) {
            commandExceptionHandler.sendNoPermissionMessage(sender);
            return;
        }
        shopGuiManager.open(target, page);
    }

    /**
     * Refreshes the daily items in the shop.
     *
     * @param sender The command sender.
     * @since 1.0
     */
    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final @NonNull BukkitCommandActor sender) {
        shopGuiManager.closeAll();
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig();
        sender.reply(messageUtils.getPrefix() + messageUtils.get("info.items-refreshed"));
        if (plugin.getConfig().getBoolean("daily-items.announce-refresh-in-chat")) {
            Bukkit.broadcastMessage(messageUtils.getPrefix() + messageUtils.get("info.shop-refreshed"));
        }
    }

    /**
     * Sends back the amount of shop uses left for a certain player.
     *
     * @param sender The command sender.
     * @param target The target player.
     * @since 1.0
     */
    @Subcommand("uses <target>")
    @CommandPermission("nozomi.uses")
    public void uses(final @NonNull BukkitCommandActor sender, final @NonNull @Default("me") Player target) {
        String messageKey = "info.shop-uses";
        String targetString = "-others";
        if (commandUtils.isSenderTarget(sender, target)) {
            targetString = "-self";
        } else if (!commandUtils.hasPermission(sender, "nozomi.uses.others")) {
            commandExceptionHandler.sendNoPermissionMessage(sender);
            return;
        }
        var uses = shopUsesService.getPlayerUses(target);
        var placeholders = new PlaceholderMap()
                .set("player", target.getName())
                .set("uses", uses);
        var unlimitedString = uses == ShopUses.UNLIMITED ? "-unlimited" : "";
        sender.reply(messageUtils.getPrefix() + messageUtils.get(
                messageKey + targetString + unlimitedString, placeholders.map()));
    }
}
