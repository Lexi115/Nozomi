package com.github.lexi115.projectNozomi;

import com.github.lexi115.projectNozomi.commands.PluginCommands;
import com.github.lexi115.projectNozomi.commands.ShopCommands;
import com.github.lexi115.projectNozomi.injection.SimpleBinderModule;
import com.github.lexi115.projectNozomi.shop.DailyItemRefreshTask;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.github.lexi115.projectNozomi.misc.ConfigUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import revxrsal.commands.bukkit.BukkitLamp;

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
    private ConfigUtils configUtils;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopGuiManager shopGuiManager;

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
        log.info("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        dailyRefreshTask.stop();
        log.info("ProjectNozomi is disabled!");
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
        dailyItemsConfig = configUtils.loadConfig("daily.yml");
        shopConfig = configUtils.loadConfig("shop.yml");
        messagesConfig = configUtils.loadConfig("messages.yml");
        log.info("Loaded configs");
    }

    private void loadShop() {
        shopGuiManager.closeAll();
        shopService.loadShopFromConfig();
        shopService.loadDailyItemsFromConfig();
        log.info("Loaded shop with {} items", shopService.getTotalItems());
        log.info("Loaded {} daily items", shopService.getTotalDailyItems());
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
