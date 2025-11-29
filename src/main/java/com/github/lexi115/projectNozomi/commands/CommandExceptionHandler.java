package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.shop.gui.InvalidPageException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidHelpPageException;
import revxrsal.commands.exception.InvalidIntegerException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.UnknownCommandException;

/**
 * Class that handles exceptions thrown during command execution.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class CommandExceptionHandler extends BukkitExceptionHandler {

    /**
     * Utility class to send formatted messages to a user.
     */
    private final MessageUtils messageUtils;

    /**
     * Constructor.
     *
     * @param messageUtils The message utility class.
     * @since 1.0
     */
    @Inject
    public CommandExceptionHandler(final MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    /**
     * Gets invoked when permission is denied to a user.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onNoPermission(final @NonNull NoPermissionException e, final @NonNull BukkitCommandActor sender) {
        sendNoPermissionMessage(sender);
    }

    /**
     * Sends a 'no permission' message to a user.
     *
     * @param recipient The recipient user.
     * @since 1.0
     */
    public void sendNoPermissionMessage(final @NonNull BukkitCommandActor recipient) {
        recipient.error(messageUtils.getPrefix() + messageUtils.get("errors.no-permission"));
    }

    /**
     * Gets invoked when the sender is not a player.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onSenderNotPlayer(final @NonNull SenderNotPlayerException e, final @NonNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.players-only"));
    }

    /**
     * Gets invoked when a user tries executing an unknown command.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onUnknownCommand(final @NonNull UnknownCommandException e, final @NonNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.unknown-command"));
    }

    /**
     * Gets invoked when a user enters an invalid integer as an argument.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onInvalidInteger(final @NonNull InvalidIntegerException e, final @NonNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-arguments"));
    }

    /**
     * Gets invoked when a target player is invalid.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onInvalidPlayer(final @NonNull InvalidPlayerException e, final @NonNull BukkitCommandActor sender) {
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-player"));
    }

    /**
     * Gets invoked when a user tries to open an invalid help page.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @Override
    public void onInvalidHelpPage(final @NonNull InvalidHelpPageException e, final @NonNull BukkitCommandActor sender) {
        var exception = new InvalidPageException(e.page(), e.numberOfPages());
        onInvalidPage(exception, sender);
    }

    /**
     * Gets invoked when a user tries to open an invalid page (whether it's the help manual or a shop GUI page).
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    @HandleException
    public void onInvalidPage(final @NonNull InvalidPageException e, final @NonNull BukkitCommandActor sender) {
        var placeholders = new PlaceholderMap()
                .set("page", e.getPage())
                .set("totalPages", e.getTotalPages());
        sender.error(messageUtils.getPrefix() + messageUtils.get("errors.invalid-page", placeholders.map()));
    }
}
