package com.github.lexi115.projectNozomi.shop;

import revxrsal.commands.exception.ThrowableFromCommand;

@ThrowableFromCommand
public class ShopUsesException extends RuntimeException {
    public ShopUsesException(final String s) {
        super(s);
    }
}
