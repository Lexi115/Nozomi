package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

@Singleton
public class ShopExceptionHandler {

    private final MessageUtils messageUtils;

    @Inject
    public ShopExceptionHandler(final MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void onNotEnoughItems(
            final @NonNull NotEnoughItemsException e, final @NonNull CommandSender sender) {
        sender.sendMessage(messageUtils.getPrefix() + messageUtils.get("errors.not-enough-items"));
    }

    public void onNoUses(
            final @NonNull NoUsesException e, final @NonNull CommandSender sender) {
        sender.sendMessage(messageUtils.getPrefix() + messageUtils.get("errors.no-more-uses"));
    }
}
