package com.github.lexi115.projectNozomi.shop;

public class NotEnoughItemsException extends RuntimeException {

    public NotEnoughItemsException(final String s) {
        super(s);
    }
}
