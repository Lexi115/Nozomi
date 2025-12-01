package com.github.lexi115.projectNozomi.shop;

/**
 * Exception thrown when a reward could not be given to the player due to an error.
 *
 * @author Lexi115
 * @since 1.0
 */
public class RewardGiveException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     * @since 1.0
     */
    public RewardGiveException(final String message) {
        super(message);
    }
}
