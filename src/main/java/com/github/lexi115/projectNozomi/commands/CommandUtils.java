package com.github.lexi115.projectNozomi.commands;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Singleton
public class CommandUtils {

    public boolean isSenderTarget(final @NonNull BukkitCommandActor sender, final @NonNull Player target) {
        if (sender.isPlayer()) {
            var senderPlayer = sender.asPlayer();
            assert senderPlayer != null;
            return senderPlayer.getUniqueId().equals(target.getUniqueId());
        }
        return false;
    }

    public boolean hasPermission(final @NonNull BukkitCommandActor sender, final @NonNull String permission) {
        if (sender.isConsole()) {
            return true;
        }
        var senderPlayer = sender.asPlayer();
        assert senderPlayer != null;
        return senderPlayer.hasPermission(permission);
    }
}
