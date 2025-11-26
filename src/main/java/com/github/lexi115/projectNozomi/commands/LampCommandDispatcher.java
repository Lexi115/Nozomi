package com.github.lexi115.projectNozomi.commands;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.command.Potential;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.UnknownCommandException;
import revxrsal.commands.node.DispatcherSettings;
import revxrsal.commands.stream.StringStream;

import java.util.List;
import java.util.Objects;

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
    public void register(final Commands object) {
        lamp.register(object);
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
            if (attempt.error() instanceof NoPermissionException) {
                var error = Objects.requireNonNull((NoPermissionException) attempt.error());
                exceptionHandler.onNoPermission(error, sender);
                return;
            }
        }
        exceptionHandler.onUnknownCommand(new UnknownCommandException(input.toString()), sender);
    }
}
