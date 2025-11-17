package io.github.lexi115.projectNozomi;

import io.github.lexi115.projectNozomi.commands.PluginCommands;
import io.github.lexi115.projectNozomi.commands.ShopCommands;
import io.github.lexi115.projectNozomi.shop.Shop;
import io.github.lexi115.projectNozomi.shop.ShopService;
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
    private FileConfiguration shopConfig;
    private FileConfiguration messagesConfig;
    private ShopService shopService;
    private Shop shop;

    @Override
    public void onEnable() {
        loadConfigs();
        loadShop();
        registerCommands();
        log.info("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        log.info("ProjectNozomi is disabled!");
    }

    public void reloadConfigs() {
        reloadConfig();
        loadConfigs();
        loadShop();
        log.info("Reloaded configs");
    }

    private void loadConfigs() {
        saveDefaultConfig();
        log.info("Loaded main config");
        shopConfig = loadCustomConfig("shop.yml");
        log.info("Loaded shop config");
        messagesConfig = loadCustomConfig("messages.yml");
        log.info("Loaded messages config");
    }

    private FileConfiguration loadCustomConfig(final String filename) {
        var configFile = new File(getDataFolder() + "/" + filename);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(filename, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
    }

    private void loadShop() {
        if (shopService == null) {
            shopService = new ShopService(this);
        }
        shop = shopService.loadShopFromConfig(shop, shopConfig);
        log.info("Loaded shop with {} items", shop.getTotalItems());
        shopService.refreshDailyItems(shop);
        log.info("Refreshed daily items");
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this)
                .dependency(Logger.class, log)
                .dependency(ShopService.class, shopService)
                .dependency(Shop.class, shop)
                .build();
        lamp.register(new PluginCommands(this));
        lamp.register(new ShopCommands());
    }
}
