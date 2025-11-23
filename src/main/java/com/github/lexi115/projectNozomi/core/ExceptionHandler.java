package com.github.lexi115.projectNozomi.core;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.shop.gui.InvalidPageException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.NoPermissionException;

@Singleton
public class ExceptionHandler extends BukkitExceptionHandler {

    private final MessageUtils messageUtils;

    @Inject
    public ExceptionHandler(final MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    @Override
    public void onNoPermission(final @NotNull NoPermissionException e, final @NotNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.no-permission"));
    }

    @Override
    public void onSenderNotPlayer(final @NonNull SenderNotPlayerException e, final @NonNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.players-only"));
    }

    @HandleException
    public void onInvalidPage(final @NonNull InvalidPageException e, final @NonNull BukkitCommandActor sender) {
        var placeholders = new PlaceholderMap()
                .set("page", e.getPage())
                .set("totalPages", e.getTotalPages())
                .get();
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-page", placeholders));
    }
}
