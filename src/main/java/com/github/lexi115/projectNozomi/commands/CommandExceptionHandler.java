package com.github.lexi115.projectNozomi.commands;

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
import revxrsal.commands.exception.InvalidHelpPageException;
import revxrsal.commands.exception.InvalidIntegerException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.UnknownCommandException;

@Singleton
public class CommandExceptionHandler extends BukkitExceptionHandler {

    private final MessageUtils messageUtils;

    @Inject
    public CommandExceptionHandler(final MessageUtils messageUtils) {
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

    @Override
    public void onUnknownCommand(final @NotNull UnknownCommandException e, final @NotNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.unknown-command"));
    }

    @Override
    public void onInvalidInteger(final @NotNull InvalidIntegerException e, final @NotNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-arguments"));
    }

    @Override
    public void onInvalidHelpPage(final @NotNull InvalidHelpPageException e, final @NotNull BukkitCommandActor sender) {
        var exception = new InvalidPageException(e.page(), e.numberOfPages());
        onInvalidPage(exception, sender);
    }

    @HandleException
    public void onInvalidPage(final @NonNull InvalidPageException e, final @NonNull BukkitCommandActor sender) {
        var placeholders = new PlaceholderMap()
                .set("page", e.getPage())
                .set("totalPages", e.getTotalPages());
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-page", placeholders.map()));
    }
}
