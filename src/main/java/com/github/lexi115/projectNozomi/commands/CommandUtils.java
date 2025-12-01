package com.github.lexi115.projectNozomi.commands;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

/**
 * Utility class for command-related operations.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class CommandUtils {

    /**
     * Checks whether the command sender is also the target player.
     *
     * @param sender The command sender.
     * @param target The target player.
     * @return <code>true</code> if sender is the target, <code>false</code> otherwise.
     * @since 1.0
     */
    public boolean isSenderTarget(final @NonNull BukkitCommandActor sender, final @NonNull Player target) {
        if (sender.isPlayer()) {
            var senderPlayer = sender.asPlayer();
            assert senderPlayer != null;
            return senderPlayer.getUniqueId().equals(target.getUniqueId());
        }
        return false;
    }

    /**
     * Check whether the command sender has a certain permission.
     *
     * @param sender     The command sender.
     * @param permission The permission name.
     * @return <code>true</code> if sender has the permission, <code>false</code> otherwise.
     * @since 1.0
     */
    public boolean hasPermission(final @NonNull BukkitCommandActor sender, final @NonNull String permission) {
        if (sender.isConsole()) {
            return true;
        }
        var senderPlayer = sender.asPlayer();
        assert senderPlayer != null;
        return senderPlayer.hasPermission(permission);
    }
}
