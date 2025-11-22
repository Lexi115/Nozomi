package com.github.lexi115.projectNozomi.misc;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

@Singleton
public class MessageUtils {

    private final ProjectNozomi plugin;

    private final StringUtils stringUtils;

    private FileConfiguration messagesConfig;

    @Inject
    public MessageUtils(final ProjectNozomi plugin, final StringUtils stringUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
    }

    public void loadConfig() {
        messagesConfig = plugin.getMessagesConfig();
    }

    public String get(final @NonNull String path) {
        return get(path, Map.of());
    }

    public String get(final @NonNull String path, final @NonNull Map<String, String> placeholders) {
        var message = messagesConfig.getString(path, "");
        return stringUtils.colorize(stringUtils.fillPlaceholders(message, placeholders));
    }
}
