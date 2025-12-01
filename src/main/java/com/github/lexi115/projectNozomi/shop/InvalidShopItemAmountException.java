package com.github.lexi115.projectNozomi.shop;

/**
 * Exception thrown when the shop item amount is invalid (mostly because it's negative).
 *
 * @author Lexi115
 * @since 1.0
 */
public class InvalidShopItemAmountException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param amount The invalid amount.
     * @since 1.0
     */
    public InvalidShopItemAmountException(final int amount) {
        super("Invalid amount: " + amount);
    }
}
