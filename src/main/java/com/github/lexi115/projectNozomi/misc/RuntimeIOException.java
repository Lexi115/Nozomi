package com.github.lexi115.projectNozomi.misc;

public class RuntimeIOException extends RuntimeException {

    public RuntimeIOException(final String message) {
        super(message);
    }

    public RuntimeIOException(final Throwable e) {
        super(e);
    }
}
