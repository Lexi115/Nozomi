package io.github.lexi115.projectNozomi;

import io.github.lexi115.projectNozomi.commands.ShopCommands;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revxrsal.commands.bukkit.BukkitLamp;

import java.io.File;

@Getter
public final class ProjectNozomi extends JavaPlugin {
    private final Logger log = LoggerFactory.getLogger(ProjectNozomi.class);
    private FileConfiguration itemsConfig;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        loadConfigs();
        registerCommands();
        log.info("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        log.info("ProjectNozomi is disabled!");
    }

    private void loadConfigs() {
        saveDefaultConfig();
        itemsConfig = loadCustomConfig("items.yml");
        messagesConfig = loadCustomConfig("messages.yml");
    }

    private FileConfiguration loadCustomConfig(final String filename) {
        var configFile = new File(getDataFolder() + "/" + filename);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this)
                .build();
        lamp.register(new ShopCommands(log));
    }
}
