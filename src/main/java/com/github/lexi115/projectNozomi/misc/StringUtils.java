package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.ChatColor;

import java.util.Map;

@Singleton
public class StringUtils {

    public String fillPlaceholders(final @NonNull String text, final @NonNull Map<String, String> placeholders) {
        return fillPlaceholders(text, placeholders, "%");
    }

    public String fillPlaceholders(
            final @NonNull String text,
            final @NonNull Map<String, String> placeholders,
            final @NonNull String delimiter
    ) {
        var newText = text;
        StringBuilder sb;
        for (var key : placeholders.keySet()) {
            sb = new StringBuilder();
            var placeholder = sb.append(delimiter).append(key).append(delimiter).toString();
            newText = newText.replaceAll(placeholder, placeholders.get(key));
        }
        return newText;
    }

    public String colorize(final @NonNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String format(final @NonNull String text) {
        return colorize(fillPlaceholders(text, Map.of()));
    }

    public String format(final @NonNull String text, final @NonNull Map<String, String> placeholders) {
        return colorize(fillPlaceholders(text, placeholders));
    }
}
