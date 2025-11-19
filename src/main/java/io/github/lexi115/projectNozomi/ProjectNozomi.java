package io.github.lexi115.projectNozomi;

import com.google.inject.Inject;
import com.google.inject.Injector;
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

    public void reloadConfigs() {
        reloadConfig();
        loadConfigs();
        loadShop();
        loadDailyRefreshTask();
        log.info("Reloaded configs");
    }

    private void loadConfigs() {
        saveDefaultConfig();
        log.info("Loaded main config");
        dailyItemsConfig = loadCustomConfig("daily.yml");
        log.info("Loaded daily config");
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
            System.out.println("shop service is null!");
        }
        if (shopGuiManager == null) {
            System.out.println("shopGuiManager is null!");
        }
        shop = shopService.loadShopFromConfig();
        shopGuiManager.closeAll();
        var dailyItems = shopService.loadDailyItemsFromConfig();
        log.info("Loaded shop with {} items", shop.getTotalItems());
        log.info("Loaded {} daily items", dailyItems.size());
    }

    private void loadDailyRefreshTask() {
        if (dailyRefreshTask == null) {
            System.out.println("daily refresh task is null!");
        }
        if (getConfig().getBoolean("daily-items.auto-refresh.enabled")) {
            dailyRefreshTask.restart();
            log.info("Restarted auto refresh task");
        } else {
            dailyRefreshTask.stop();
            log.info("Stopped auto refresh task");
        }
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this)
                .dependency(Logger.class, log)
                .dependency(ShopService.class, shopService)
                .dependency(ShopGuiManager.class, shopGuiManager)
                .build();
        lamp.register(new PluginCommands(this));
        lamp.register(new ShopCommands(this));
    }
}
