package com.github.lexi115.projectNozomi;

import com.github.lexi115.projectNozomi.commands.PluginCommands;
import com.github.lexi115.projectNozomi.commands.ShopCommands;
import com.github.lexi115.projectNozomi.ext.VaultExtension;
import com.github.lexi115.projectNozomi.injection.SimpleBinderModule;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.github.lexi115.projectNozomi.misc.ConfigUtils;
import com.github.lexi115.projectNozomi.tasks.Task;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import revxrsal.commands.bukkit.BukkitLamp;

@Singleton
@Getter
public final class ProjectNozomi extends JavaPlugin {

    private SimpleBinderModule binderModule;

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

    @Inject @Named("dailyRefreshTask")
    private Task dailyRefreshTask;

    private VaultExtension vault;

    @Override
    public void onEnable() {
        injectFields();
        loadConfigs();
        loadShop();
        loadDailyRefreshTask();
        loadExtensions();
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

    private void injectFields() {
        binderModule = new SimpleBinderModule(this);
        injector = binderModule.createInjector();
        injector.injectMembers(this);
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

    private void loadExtensions() {
        vault = new VaultExtension(this);
        if (!vault.setup()) {
            log.info("vault not found");
        } else {
            log.info("vault loaded!");
        }
    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this).build();
        lamp.register(injector.getInstance(PluginCommands.class));
        lamp.register(injector.getInstance(ShopCommands.class));
    }
}
