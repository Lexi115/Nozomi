package com.github.lexi115.projectNozomi.shop.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import revxrsal.commands.exception.ThrowableFromCommand;

/**
 * Exception thrown when trying to view an invalid page.
 *
 * @author Lexi115
 * @since 1.0
 */
@ThrowableFromCommand
@AllArgsConstructor
@Getter
public class InvalidPageException extends RuntimeException {

    /**
     * Invalid page number.
     */
    private final int page;

    /**
     * Total number of pages.
     */
    private final int totalPages;
}
