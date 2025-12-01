package com.github.lexi115.projectNozomi.shop;

/**
 * Exception thrown when some daily item slots could not be filled due to a lack of items in the shop.
 *
 * @author Lexi115
 * @since 1.0
 */
public class NotEnoughShopItemsException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     * @since 1.0
     */
    public NotEnoughShopItemsException(final String message) {
        super(message);
    }
}
