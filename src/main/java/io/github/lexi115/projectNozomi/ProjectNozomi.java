package io.github.lexi115.projectNozomi;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.github.lexi115.projectNozomi.commands.PluginCommands;
import io.github.lexi115.projectNozomi.commands.ShopCommands;
import io.github.lexi115.projectNozomi.injection.SimpleBinderModule;
import io.github.lexi115.projectNozomi.shop.DailyItemRefreshTask;
import io.github.lexi115.projectNozomi.shop.Shop;
import io.github.lexi115.projectNozomi.shop.ShopService;
import io.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import revxrsal.commands.bukkit.BukkitLamp;

import java.io.File;

@Singleton
@Getter
public final class ProjectNozomi extends JavaPlugin {

    private Injector injector;

    private FileConfiguration dailyItemsConfig;

    private FileConfiguration shopConfig;

    private FileConfiguration messagesConfig;

    @Inject
    private Logger log;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopGuiManager shopGuiManager;

    @Inject
    private Shop shop;

    @Inject
    private DailyItemRefreshTask dailyRefreshTask;

    @Override
    public void onEnable() {
        var module = new SimpleBinderModule(this);
        injector = module.createInjector();
        injector.injectMembers(this);
        loadConfigs();
        loadShop();
        loadDailyRefreshTask();
        registerCommands();
        log.info("ProjectNozomi is enabled!!!");
    }

    @Override
    public void onDisable() {
        dailyRefreshTask.stop();
        log.info("ProjectNozomi is disabled!!!");
    }

    public void reloadPlugin() {
        reloadConfig();
        loadConfigs();
        loadShop();
        loadDailyRefreshTask();
        log.info("Reloaded plugin");
    }

    private void loadConfigs() {
        saveDefaultConfig();
        dailyItemsConfig = loadCustomConfig("daily.yml");
        shopConfig = loadCustomConfig("shop.yml");
        messagesConfig = loadCustomConfig("messages.yml");
        log.info("Loaded configs");
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
        shopGuiManager.closeAll();
        shop = shopService.loadShopFromConfig();
        var dailyItems = shopService.loadDailyItemsFromConfig();
        log.info("Loaded shop with {} items", shop.getTotalItems());
        log.info("Loaded {} daily items", dailyItems.size());
    }

    private void loadDailyRefreshTask() {
        if (getConfig().getBoolean("daily-items.auto-refresh.enabled")) {
            dailyRefreshTask.restart();
            log.info("Restarted auto refresh task");
        } else {
            dailyRefreshTask.stop();
            log.info("Stopped auto refresh task");
        }
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(injector.getInstance(PluginCommands.class));
        lamp.register(injector.getInstance(ShopCommands.class));
    }
}
