package io.github.lexi115.projectNozomi.shop;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Reward {

    default String fillPlaceholders(final String text, final Map<String, String> placeholders) {
        var newText = text;
        for (var key : placeholders.keySet()) {
            newText = newText.replaceAll("%" + key + "%", placeholders.get(key));
        }
        return newText;
    }

    void give(Player player, Map<String, String> placeholders);
}
