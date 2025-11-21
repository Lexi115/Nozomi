package com.github.lexi115.projectNozomi.misc;

public class SaveFileException extends RuntimeException {

    public SaveFileException(final String message) {
        super(message);
    }

    public SaveFileException(final Throwable e) {
        super(e);
    }
}
