package com.github.lexi115.projectNozomi.commands;

import com.google.inject.Inject;
import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import lombok.NonNull;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"nozomi", "noz"})
public class ShopCommands {

    private final ProjectNozomi plugin;

    private final ShopService shopService;

    private final ShopGuiManager shopGuiManager;

    @Inject
    public ShopCommands(
            final ProjectNozomi plugin,
            final ShopService shopService,
            final ShopGuiManager shopGuiManager
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.shopGuiManager = shopGuiManager;
    }

    @Subcommand("daily <page>")
    @CommandPermission("nozomi.daily")
    public void daily(final @NonNull Player sender, @Default("1") @Range(min = 1) final int page) {
        shopGuiManager.open(sender, page);
    }

    @Subcommand("refresh")
    @CommandPermission("nozomi.refresh")
    public void refresh(final @NonNull BukkitCommandActor sender) {
        shopGuiManager.closeAll();
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig();
    }
}
