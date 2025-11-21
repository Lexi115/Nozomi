package com.github.lexi115.projectNozomi.misc;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@Singleton
public class ConfigUtils {

    private final ProjectNozomi plugin;

    @Inject
    public ConfigUtils(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    public @NonNull FileConfiguration loadConfig(final @NonNull String filename) {
        var dataFolder = plugin.getDataFolder();
        var configFile = new File(dataFolder + "/" + filename);
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new SaveFileException("Could not create data folder!");
        }
        if (!configFile.exists()) {
            plugin.saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }
}
