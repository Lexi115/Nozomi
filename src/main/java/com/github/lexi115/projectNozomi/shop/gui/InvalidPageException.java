package com.github.lexi115.projectNozomi.shop.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revxrsal.commands.exception.ThrowableFromCommand;

@ThrowableFromCommand
@AllArgsConstructor
@Getter
public class InvalidPageException extends RuntimeException {

    private final int page;

    private final int totalPages;
}
