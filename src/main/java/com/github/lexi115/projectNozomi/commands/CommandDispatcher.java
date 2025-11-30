package com.github.lexi115.projectNozomi.commands;

/**
 * A command dispatcher interface.
 *
 * @author Lexi115
 * @since 1.0
 */
public interface CommandDispatcher {
    /**
     * Sets up the dispatcher.
     *
     * @since 1.0
     */
    void setup();

    /**
     * Registers a set of commands.
     *
     * @param commands The commands to register.
     * @since 1.0
     */
    void register(Commands commands);
}
