package com.github.lexi115.projectNozomi.shop;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(final int amount) {
        super("Invalid amount: " + amount);
    }
}
