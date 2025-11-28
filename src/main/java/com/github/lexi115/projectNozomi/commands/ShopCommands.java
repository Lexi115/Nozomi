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

    private final CommandExceptionHandler commandExceptionHandler;

    @Inject
    public ShopCommands(
            final ShopService shopService,
            final ShopUsesService shopUsesService,
            final ShopGuiManager shopGuiManager,
            final MessageUtils messageUtils,
            final CommandExceptionHandler commandExceptionHandler
    ) {
        this.shopService = shopService;
        this.shopUsesService = shopUsesService;
        this.shopGuiManager = shopGuiManager;
        this.messageUtils = messageUtils;
        this.commandExceptionHandler = commandExceptionHandler;
    }

    @Subcommand("daily <page>")
    @CommandPermission("nozomi.daily")
    @Description("This opens the shop")
    public void daily(final @NonNull Player sender, @Default("1") @Range(min = 1) final int page) {
        shopGuiManager.open(sender, page);
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
        if (sender.isPlayer()) {
            var senderPlayer = sender.asPlayer();
            assert senderPlayer != null;
            if (senderPlayer.getUniqueId().equals(target.getUniqueId())) {
                targetString = "-self";
            } else if (!senderPlayer.hasPermission("nozomi.uses.others")) {
                commandExceptionHandler.onNoPermission(null, sender);
                return;
            }
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
