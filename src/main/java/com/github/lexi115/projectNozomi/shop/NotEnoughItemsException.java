package com.github.lexi115.projectNozomi.shop;

import revxrsal.commands.exception.ThrowableFromCommand;

@ThrowableFromCommand
public class NotEnoughItemsException extends RuntimeException {

    public NotEnoughItemsException(final String s) {
        super(s);
    }
}
