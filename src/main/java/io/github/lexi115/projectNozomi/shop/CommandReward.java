package io.github.lexi115.projectNozomi.shop;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class CommandReward implements Reward {

    private final String rawCommand;

    @Override
    public void give(final Player player, final Map<String, String> placeholders) {
        var command = fillPlaceholders(rawCommand, placeholders);
        if (command.startsWith("/")) { // Player command
            player.performCommand(command.replaceFirst("/", ""));
        } else { // Console command
            var console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, command);
        }
    }
}
