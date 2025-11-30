package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.Potential;
import revxrsal.commands.exception.InvalidIntegerException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.UnknownCommandException;
import revxrsal.commands.node.DispatcherSettings;
import revxrsal.commands.stream.StringStream;

import java.util.List;

/**
 * Command dispatcher managed by the Lamp framework.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class LampCommandDispatcher implements CommandDispatcher {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The command exception handler.
     */
    private final CommandExceptionHandler commandExceptionHandler;

    /**
     * The Lamp framework object.
     */
    private Lamp<BukkitCommandActor> lamp;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param commandExceptionHandler The command exception handler.
     * @since 1.0
     */
    @Inject
    public LampCommandDispatcher(final ProjectNozomi plugin, final CommandExceptionHandler commandExceptionHandler) {
        this.plugin = plugin;
        this.commandExceptionHandler = commandExceptionHandler;
    }

    /**
     * Sets up the dispatcher.
     *
     * @since 1.0
     */
    @Override
    public void setup() {
        if (this.lamp == null) {
            this.lamp = BukkitLamp.builder(plugin)
                    .exceptionHandler(commandExceptionHandler)
                    .dispatcherSettings(this::getDispatcherSettings)
                    .build();
        }
    }

    /**
     * Registers a set of commands.
     *
     * @param commands The commands to register.
     * @since 1.0
     */
    @Override
    public void register(final Commands commands) {
        lamp.register(commands);
    }

    private void getDispatcherSettings(
            final @NonNull DispatcherSettings.Builder<BukkitCommandActor> settings) {
        var constantsConfig = plugin.getConstantsConfig();
        settings
                .maximumFailedAttempts(constantsConfig.getInt("command-dispatcher.maximum-failed-attempts"))
                .failureHandler(this::getFailureHandler);
    }

    private void getFailureHandler(
            final BukkitCommandActor sender,
            final @NonNull List<Potential<BukkitCommandActor>> failedAttempts,
            final StringStream input
    ) {
        for (var attempt : failedAttempts) {
            var error = attempt.error();
            if (error instanceof NoPermissionException) {
                handleError(NoPermissionException.class, error, sender, commandExceptionHandler::onNoPermission);
                return;
            } else if (error instanceof InvalidPlayerException) {
                handleError(InvalidPlayerException.class, error, sender, commandExceptionHandler::onInvalidPlayer);
                return;
            } else if (error instanceof InvalidIntegerException) {
                handleError(InvalidIntegerException.class, error, sender, commandExceptionHandler::onInvalidInteger);
                return;
            } else if (error instanceof SenderNotPlayerException) {
                handleError(SenderNotPlayerException.class, error, sender, commandExceptionHandler::onSenderNotPlayer);
                return;
            }
        }
        commandExceptionHandler.onUnknownCommand(new UnknownCommandException(input.toString()), sender);
    }

    private <T extends Throwable> void handleError(
            final @NonNull Class<T> clazz,
            final Throwable e,
            final BukkitCommandActor actor,
            final Handler<T> handler) {
        if (clazz.isInstance(e)) {
            handler.handle(clazz.cast(e), actor);
        }
    }

    interface Handler<T extends Throwable> {
        void handle(T e, BukkitCommandActor actor);
    }
}
