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

@Singleton
public class LampCommandDispatcher implements CommandDispatcher {

    private final ProjectNozomi plugin;

    private final CommandExceptionHandler exceptionHandler;

    private Lamp<BukkitCommandActor> lamp;

    @Inject
    public LampCommandDispatcher(final ProjectNozomi plugin, final CommandExceptionHandler exceptionHandler) {
        this.plugin = plugin;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void setup() {
        if (this.lamp == null) {
            this.lamp = BukkitLamp.builder(plugin)
                    .exceptionHandler(exceptionHandler)
                    .dispatcherSettings(this::getDispatcherSettings)
                    .build();
        }
    }

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
                handleException(NoPermissionException.class, error, sender, exceptionHandler::onNoPermission);
                return;
            } else if (error instanceof InvalidPlayerException) {
                handleException(InvalidPlayerException.class, error, sender, exceptionHandler::onInvalidPlayer);
                return;
            } else if (error instanceof InvalidIntegerException) {
                handleException(InvalidIntegerException.class, error, sender, exceptionHandler::onInvalidInteger);
                return;
            } else if (error instanceof SenderNotPlayerException) {
                handleException(SenderNotPlayerException.class, error, sender, exceptionHandler::onSenderNotPlayer);
                return;
            }
        }
        exceptionHandler.onUnknownCommand(new UnknownCommandException(input.toString()), sender);
    }

    private <T extends Throwable> void handleException(
            final @NonNull Class<T> clazz,
            final Throwable e,
            final BukkitCommandActor actor,
            final HandlerMethod<T> method) {
        if (clazz.isInstance(e)) {
            method.handle(clazz.cast(e), actor);
        }
    }

    interface HandlerMethod<T extends Throwable> {
        void handle(T e, BukkitCommandActor actor);
    }
}
