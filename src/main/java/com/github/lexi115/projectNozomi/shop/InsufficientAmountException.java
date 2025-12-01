package com.github.lexi115.projectNozomi.shop;

/**
 * Exception thrown when a player does not have the required amount in his inventory to sell an item.
 *
 * @author Lexi115
 * @since 1.0
 */
public class InsufficientAmountException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     * @since 1.0
     */
    public InsufficientAmountException(final String message) {
        super(message);
    }
}
