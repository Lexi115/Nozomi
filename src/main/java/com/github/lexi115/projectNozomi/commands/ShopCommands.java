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
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Singleton
@Command({"noz"})
public class ShopCommands implements Commands {

    private final ShopService shopService;

    private final ShopUsesService shopUsesService;

    private final ShopGuiManager shopGuiManager;

    private final MessageUtils messageUtils;

    private final CommandUtils commandUtils;

    private final CommandExceptionHandler commandExceptionHandler;

    @Inject
    public ShopCommands(
            final ShopService shopService,
            final ShopUsesService shopUsesService,
            final ShopGuiManager shopGuiManager,
            final MessageUtils messageUtils,
            final CommandUtils commandUtils,
            final CommandExceptionHandler commandExceptionHandler
    ) {
        this.shopService = shopService;
        this.shopUsesService = shopUsesService;
        this.shopGuiManager = shopGuiManager;
        this.messageUtils = messageUtils;
        this.commandUtils = commandUtils;
        this.commandExceptionHandler = commandExceptionHandler;
    }

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

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final @NonNull BukkitCommandActor sender) {
        shopGuiManager.closeAll();
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig();
        sender.reply(messageUtils.getPrefix() + messageUtils.get("info.items-refreshed"));
    }

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
