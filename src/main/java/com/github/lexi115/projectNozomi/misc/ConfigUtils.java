package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

/**
 * Utility class used for configuration-related operations, like saving and loading custom plugin
 * configs (for example message files).
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class ConfigUtils {

    /**
     * The plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @since 1.0
     */
    @Inject
    public ConfigUtils(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves a config resource file into the plugin's data folder (if it doesn't exist yet), then loads it as a
     * {@link FileConfiguration}.
     *
     * @param filename The filename of the resource to copy.
     * @return The related configuration object.
     * @throws RuntimeIOException if the resource file could not be found or another I/O error occurred.
     * @since 1.0
     */
    public @NonNull FileConfiguration saveAndLoadConfig(final @NonNull String filename) {
        var configFile = new File(plugin.getDataFolder() + "/" + filename);
        if (!configFile.exists()) {
            try {
                plugin.saveResource(filename, false);
            } catch (IllegalArgumentException e) {
                throw new RuntimeIOException(e);
            }
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * Loads a resource config file as a {@link FileConfiguration} (but doesn't save it in the plugin's data folder).
     *
     * @param filename The filename of the resource to load.
     * @return The related configuration object.
     * @throws RuntimeIOException if the resource file could not be found or another I/O error occurred.
     * @since 1.0
     */
    public @NonNull FileConfiguration loadConfig(final @NonNull String filename) {
        var stream = plugin.getResource(filename);
        if (stream == null) {
            throw new RuntimeIOException("Could not load resource stream!");
        }
        var reader = new InputStreamReader(stream);
        return YamlConfiguration.loadConfiguration(reader);
    }
}
