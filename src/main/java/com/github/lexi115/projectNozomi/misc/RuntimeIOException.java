package com.github.lexi115.projectNozomi.misc;

/**
 * Exception thrown when a runtime I/O error occurs.
 *
 * @author Lexi115
 * @since 1.0
 */
public class RuntimeIOException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param message The error message.
     * @since 1.0
     */
    public RuntimeIOException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e The throwable object.
     * @since 1.0
     */
    public RuntimeIOException(final Throwable e) {
        super(e);
    }
}
