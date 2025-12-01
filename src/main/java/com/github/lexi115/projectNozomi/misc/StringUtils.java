package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.ChatColor;

import java.util.Map;

/**
 * Utility class for recurring string-related operations when working with Bukkit (like filling placeholders and
 * adding color codes).
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class StringUtils {

    /**
     * Fills placeholders in a given text. Placeholders are delimited by two percentage symbols like
     * <code>%this%</code>.
     *
     * @param text         The original text string.
     * @param placeholders The placeholders map. For example, if the key-value pair looks like
     *                     <code>"player" -> "nozomi"</code>, any <code>%player%</code> placeholder will be replaced
     *                     with <code>nozomi</code>.
     * @return The formatted string.
     * @since 1.0
     */
    public String fillPlaceholders(final @NonNull String text, final @NonNull Map<String, String> placeholders) {
        return fillPlaceholders(text, placeholders, "%");
    }

    /**
     * Fills placeholders in a given text.
     *
     * @param text The original text string.
     * @param placeholders The placeholders map. For example (assuming the delimiter used is <code>%</code>),
     *                     if the key-value pair looks like <code>"player" -> "nozomi"</code>, any
     *                     <code>%player%</code> placeholder will be replaced with <code>nozomi</code>.
     * @param delimiter The placeholder delimiter.
     * @return The formatted string.
     * @since 1.0
     */
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

    /**
     * Converts color codes inside a given text into actual colors. Color codes start with
     * an ampersand (<code>&</code>) followed by <code>0-9</code>, <code>a-f</code>, <code>k-o</code>
     * or <code>r</code> (example: <code>&6</code> for gold color).
     *
     * @param text The original text string.
     * @return The formatted string.
     * @since 1.0
     */
    public String colorize(final @NonNull String text) {
        return colorize(text, '&');
    }

    /**
     * Converts color codes inside a given text into actual colors. Color codes start with
     * a <code>colorChar</code> followed by <code>0-9</code>, <code>a-f</code>, <code>k-o</code>
     * or <code>r</code> (example: <code>&6</code> for gold color, if <code>colorChar</code> is the ampersand).
     *
     * @param text The original text string.
     * @param colorChar The character that precedes a color code.
     * @return The formatted string.
     * @since 1.0
     */
    public String colorize(final @NonNull String text, final char colorChar) {
        return ChatColor.translateAlternateColorCodes(colorChar, text);
    }

    /**
     * Fills placeholders (delimited by two percentage signs like <code>%this%</code>) in a given text and
     * converts color codes (starting with ampersand (<code>&</code>)) into actual colors. It is equivalent to:
     * <pre>
     * {@code
     * colorize(fillPlaceholders(text, placeholders));
     * }
     * </pre>
     * @param text The original text string.
     * @param placeholders The placeholders map.
     * @return The formatted string.
     * @since 1.0
     */
    public String format(final @NonNull String text, final @NonNull Map<String, String> placeholders) {
        return colorize(fillPlaceholders(text, placeholders));
    }

    /**
     * Turns a raw <code>snake_case</code> string into a more user-friendly representation, useful when
     * converting item IDs to their respective in-game names (for example, <code>ACACIA_LOG</code>
     * becomes <code>Acacia Log</code>).
     *
     * @param raw The original string.
     * @return The user-friendly equivalent string.
     * @since 1.0
     */
    public String toUserFriendly(final @NonNull String raw) {
        var split = raw.trim().split("_");
        for (int i = 0; i < split.length; i++) {
            split[i] = capitalize(split[i].toLowerCase());
        }
        return String.join(" ", split);
    }

    /**
     * Capitalizes the first letter of each word in a string (doesn't work with accents or letter variations).
     *
     * @param text The original string.
     * @return The formatted string.
     * @since 1.0
     */
    public String capitalize(final @NonNull String text) {
        if (text.isBlank()) {
            return text;
        }
        char firstChar = text.charAt(0);
        if (firstChar >= 97 && firstChar <= 122) {
            return (char) (firstChar - 32) + (text.length() > 1 ? text.substring(1) : "");
        }
        return text;
    }
}
