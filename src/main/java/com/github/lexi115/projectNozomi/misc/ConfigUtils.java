package com.github.lexi115.projectNozomi.misc;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;

@Singleton
public class ConfigUtils {

    private final ProjectNozomi plugin;

    @Inject
    public ConfigUtils(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    public @NonNull FileConfiguration saveAndLoadConfig(final @NonNull String filename) {
        var dataFolder = plugin.getDataFolder();
        var configFile = new File(dataFolder + "/" + filename);
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new RuntimeIOException("Could not create data folder!");
        }
        if (!configFile.exists()) {
            plugin.saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public @NonNull FileConfiguration loadConfig(final @NonNull String filename) {
        var stream = plugin.getResource(filename);
        if (stream == null) {
            throw new RuntimeIOException("Could not load resource stream!");
        }
        var reader = new InputStreamReader(stream);
        return YamlConfiguration.loadConfiguration(reader);
    }
}
