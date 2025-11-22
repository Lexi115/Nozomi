package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.misc.StringUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class CommandReward implements Reward {

    private final StringUtils stringUtils;

    private final String rawCommand;

    @Override
    public void give(final @NonNull Player player, final Map<String, String> placeholders) {
        var command = stringUtils.fillPlaceholders(rawCommand, placeholders);
        // If command starts with a slash, then it is executed by the player rather than the console
        if (command.startsWith("/")) {
            player.performCommand(command.replaceFirst("/", ""));
        } else {
            var console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        }
    }
}
