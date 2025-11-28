package com.github.lexi115.projectNozomi.shop;

import revxrsal.commands.exception.ThrowableFromCommand;

@ThrowableFromCommand
public class NoUsesException extends RuntimeException {

    public NoUsesException(final String s) {
        super(s);
    }
}
