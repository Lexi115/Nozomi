package com.github.lexi115.projectNozomi.shop;

/**
 * Exception thrown when a player does not have any shop uses left.
 *
 * @author Lexi115
 * @since 1.0
 */
public class NoShopUsesLeftException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     * @since 1.0
     */
    public NoShopUsesLeftException(final String message) {
        super(message);
    }
}
