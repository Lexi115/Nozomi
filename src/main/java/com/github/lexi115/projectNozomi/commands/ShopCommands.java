package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
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

    private final ShopGuiManager shopGuiManager;

    private final MessageUtils messageUtils;

    @Inject
    public ShopCommands(
            final ShopService shopService,
            final ShopGuiManager shopGuiManager,
            final MessageUtils messageUtils
    ) {
        this.shopService = shopService;
        this.shopGuiManager = shopGuiManager;
        this.messageUtils = messageUtils;
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
}
