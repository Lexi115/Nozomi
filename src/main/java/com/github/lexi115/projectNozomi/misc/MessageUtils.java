package com.github.lexi115.projectNozomi.misc;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

/**
 * Utility class used to easily get formatted message strings to send.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class MessageUtils {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The string utility class.
     */
    private final StringUtils stringUtils;

    /**
     * The message configuration.
     */
    private FileConfiguration messagesConfig;

    /**
     * The message prefix, usually the plugin's name.
     */
    @Getter
    private String prefix;

    /**
     * Constructor.
     *
     * @param plugin      The plugin instance.
     * @param stringUtils The string utility class.
     * @since 1.0
     */
    @Inject
    public MessageUtils(final ProjectNozomi plugin, final StringUtils stringUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
        this.prefix = "";
    }

    /**
     * Loads the message config (or reloads it so that after a plugin reload the newly edited messages and prefix
     * are loaded instead).
     *
     * @since 1.0
     */
    public void loadConfig() {
        messagesConfig = plugin.getMessagesConfig();
        prefix = get("prefix");
    }

    /**
     * Returns the formatted version of the string at the specified path in the config. If the path is not found,
     * an empty string ("") is returned.
     *
     * @param path The string path.
     * @return The formatted string.
     * @since 1.0
     */
    public String get(final @NonNull String path) {
        return get(path, "", Map.of());
    }

    /**
     * Returns the formatted version of the string at the specified path in the config.
     *
     * @param path The string path.
     * @param defaultString The fallback string in case the provided path could not be found.
     * @return The formatted string.
     * @since 1.0
     */
    public String get(final @NonNull String path, final @NonNull String defaultString) {
        return get(path, defaultString, Map.of());
    }

    /**
     * Returns the formatted version of the string at the specified path in the config. If the path is not found,
     * an empty string ("") is returned.
     *
     * @param path The string path.
     * @param placeholders The placeholders map.
     * @return The formatted string.
     * @since 1.0
     */
    public String get(final @NonNull String path, final @NonNull Map<String, String> placeholders) {
        return get(path, "", placeholders);
    }

    /**
     * Returns the formatted version of the string at the specified path in the config.
     *
     * @param path The string path.
     * @param defaultString The fallback string in case the provided path could not be found.
     * @param placeholders The placeholders map.
     * @return The formatted string.
     * @since 1.0
     */
    public String get(
            final @NonNull String path,
            final @NonNull String defaultString,
            final @NonNull Map<String, String> placeholders
    ) {
        var message = messagesConfig.getString(path, defaultString);
        return stringUtils.format(message, placeholders);
    }
}
