package com.github.lexi115.projectNozomi.misc;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

@Singleton
public class MessageUtils {

    private final ProjectNozomi plugin;

    private final StringUtils stringUtils;

    private FileConfiguration messagesConfig;

    @Getter
    private String prefix;

    @Inject
    public MessageUtils(final ProjectNozomi plugin, final StringUtils stringUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
        this.prefix = "";
    }

    public void loadConfig() {
        messagesConfig = plugin.getMessagesConfig();
        prefix = get("prefix");
    }

    public String get(final @NonNull String path) {
        return get(path, "", Map.of());
    }

    public String get(final @NonNull String path, final @NonNull String defaultString) {
        return get(path, defaultString, Map.of());
    }

    public String get(final @NonNull String path, final @NonNull Map<String, String> placeholders) {
        return get(path, "", placeholders);
    }

    public String get(
            final @NonNull String path,
            final @NonNull String defaultString,
            final @NonNull Map<String, String> placeholders
    ) {
        var message = messagesConfig.getString(path, defaultString);
        return stringUtils.format(message, placeholders);
    }
}
