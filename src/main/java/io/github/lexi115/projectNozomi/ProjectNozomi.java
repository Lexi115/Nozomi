package io.github.lexi115.projectNozomi;

import io.github.lexi115.projectNozomi.commands.ShopCommands;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

import java.io.File;

@Getter
public final class ProjectNozomi extends JavaPlugin {

    private static ProjectNozomi instance;

    private FileConfiguration itemsConfig;
    private FileConfiguration messagesConfig;

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        registerCommands();
        System.out.println("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("ProjectNozomi is disabled!");
    }

    private void loadConfigs() {
        saveDefaultConfig();
        itemsConfig = loadCustomConfig("items.yml");
        messagesConfig = loadCustomConfig("messages.yml");
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this)
                .build();
        lamp.register(new ShopCommands(this));
    }

    private FileConfiguration loadCustomConfig(final String filename) {
        var configFile = new File(getDataFolder() + "/" + filename);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }
}
