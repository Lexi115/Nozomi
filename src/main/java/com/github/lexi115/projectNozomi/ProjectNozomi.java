package com.github.lexi115.projectNozomi;

import com.djaytan.bukkit.slf4j.api.BukkitLoggerFactory;
import com.github.lexi115.projectNozomi.commands.CommandDispatcher;
import com.github.lexi115.projectNozomi.commands.PluginCommands;
import com.github.lexi115.projectNozomi.commands.ShopCommands;
import com.github.lexi115.projectNozomi.extensions.VaultExtension;
import com.github.lexi115.projectNozomi.injection.SimpleBinderModule;
import com.github.lexi115.projectNozomi.misc.ConfigUtils;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.misc.PlaceholderMap;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.github.lexi115.projectNozomi.tasks.Task;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

@Singleton
@Getter
public final class ProjectNozomi extends JavaPlugin {

    private Injector injector;

    private FileConfiguration constantsConfig;

    private FileConfiguration dailyItemsConfig;

    private FileConfiguration shopConfig;

    private FileConfiguration messagesConfig;

    private VaultExtension vaultExtension;

    @Inject
    private Logger log;

    @Inject
    private StringUtils stringUtils;

    @Inject
    private ConfigUtils configUtils;

    @Inject
    private MessageUtils messageUtils;

    @Inject
    private ShopService shopService;

    @Inject
    private ShopGuiManager shopGuiManager;

    @Inject
    @Named("dailyRefreshTask")
    private Task dailyRefreshTask;

    @Inject
    @Named("lamp")
    private CommandDispatcher commandDispatcher;

    @Override
    public void onEnable() {
        initLogger();
        injectFields();
        loadConfigs();
        printStartupBanner();
        loadExtensions();
        loadShop();
        loadTasks();
        registerCommands();
        log.info("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        dailyRefreshTask.stop();
        log.info("ProjectNozomi is disabled!");
    }

    private void initLogger() {
        BukkitLoggerFactory.provideBukkitLogger(this.getLogger());
    }

    public void reloadPlugin() {
        reloadConfig();
        loadConfigs();
        loadShop();
        loadTasks();
        log.info("Reloaded plugin");
    }

    private void injectFields() {
        var binderModule = new SimpleBinderModule(this);
        injector = binderModule.createInjector();
        injector.injectMembers(this);
    }

    private void loadConfigs() {
        saveDefaultConfig();
        if (constantsConfig == null) {
            constantsConfig = configUtils.loadConfig("constants.yml");
        }
        dailyItemsConfig = configUtils.saveAndLoadConfig("daily.yml");
        shopConfig = configUtils.saveAndLoadConfig("shop.yml");
        messagesConfig = configUtils.saveAndLoadConfig("messages.yml");
        messageUtils.loadConfig();
        log.info("Loaded configs");
    }

    private void printStartupBanner() {
        var bannerRows = constantsConfig.getStringList("info.startup-banner");
        var placeholders = new PlaceholderMap().set("version", getDescription().getVersion());
        bannerRows.forEach(row -> log.info(stringUtils.format(row, placeholders.map())));
    }

    private void loadExtensions() {
        // Vault API (used for money rewards)
        vaultExtension = new VaultExtension(this);
        if (vaultExtension.setup()) {
            log.info("Vault loaded");
        }
    }

    private void loadShop() {
        shopGuiManager.closeAll();
        shopService.loadShopFromConfig();
        shopService.loadDailyItemsFromConfig();
        log.info("Loaded shop with {} items", shopService.getTotalItems());
        log.info("Loaded {} daily items", shopService.getTotalDailyItems());
    }

    private void loadTasks() {
        // Daily item refresh
        if (getConfig().getBoolean("daily-items.auto-refresh.enabled")) {
            dailyRefreshTask.restart();
            log.info("Restarted auto refresh task");
        } else {
            dailyRefreshTask.stop();
            log.info("Stopped auto refresh task");
        }
    }

    private void registerCommands() {
        commandDispatcher.setup();
        commandDispatcher.register(injector.getInstance(PluginCommands.class));
        commandDispatcher.register(injector.getInstance(ShopCommands.class));
    }
}
