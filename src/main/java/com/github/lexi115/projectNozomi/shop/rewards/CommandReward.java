package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.misc.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Reward that consists of executing a player or console command.
 *
 * @author Lexi115
 * @since 1.0
 */
@AllArgsConstructor
public class CommandReward implements Reward {

    /**
     * The string utility class.
     */
    private final StringUtils stringUtils;

    /**
     * The raw command string with placeholders not yet parsed.
     */
    private final String rawCommand;

    /**
     * Gives the reward to the specified player.
     *
     * @param player       The target player
     * @param placeholders The placeholders map.
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @since 1.0
     */
    @Override
    public boolean give(final @NonNull Player player, final Map<String, String> placeholders) {
        var command = stringUtils.fillPlaceholders(rawCommand, placeholders);
        // If command starts with a slash, then it is executed by the player rather than the console
        if (command.startsWith("/")) {
            return player.performCommand(command.replaceFirst("/", ""));
        } else {
            var console = Bukkit.getConsoleSender();
            return Bukkit.dispatchCommand(console, command);
        }
    }
}
