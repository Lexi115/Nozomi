package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

/**
 * Class that handles exceptions thrown during shop-related operations.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class ShopExceptionHandler {

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
    public ShopExceptionHandler(final MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    /**
     * Gets invoked when a user does not have the required amount when trying to sell an item.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    public void onNotEnoughItems(final @NonNull NotEnoughItemsException e, final @NonNull CommandSender sender) {
        sender.sendMessage(messageUtils.getPrefix() + messageUtils.get("errors.not-enough-items"));
    }

    /**
     * Gets invoked when a user has no more shop uses.
     *
     * @param e The exception.
     * @param sender The victim user.
     * @since 1.0
     */
    public void onNoUses(final @NonNull NoUsesException e, final @NonNull CommandSender sender) {
        sender.sendMessage(messageUtils.getPrefix() + messageUtils.get("errors.no-more-uses"));
    }
}
