package com.github.lexi115.projectNozomi;

import com.djaytan.bukkit.slf4j.api.BukkitLoggerFactory;
import com.github.lexi115.projectNozomi.commands.CommandDispatcher;
import com.github.lexi115.projectNozomi.commands.PluginCommands;
import com.github.lexi115.projectNozomi.commands.ShopCommands;
import com.github.lexi115.projectNozomi.database.DatabaseManager;
import com.github.lexi115.projectNozomi.extensions.VaultExtension;
import com.github.lexi115.projectNozomi.injection.SimpleBinderModule;
import com.github.lexi115.projectNozomi.misc.*;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.github.lexi115.projectNozomi.tasks.Task;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.sql.SQLException;

/**
 * ProjectNozomi is the entrypoint for this plugin. It loads config files from the JAR archive and saves them in the
 * plugin's data folder, sets up database connection, and loads the actual item shop.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
@Getter
public final class ProjectNozomi extends JavaPlugin {

    /**
     * The dependency injector.
     */
    private Injector injector;

    /**
     * The config containing useful constant values used throughout the project.
     */
    private FileConfiguration constantsConfig;

    /**
     * The config containing the current (daily) items in the shop.
     */
    private FileConfiguration dailyItemsConfig;

    /**
     * The config containing information about the shop and its items.
     */
    private FileConfiguration shopConfig;

    /**
     * The config containing user-friendly messages sent to the user after performing a command or action.
     */
    private FileConfiguration messagesConfig;

    /**
     * The Vault API extension, used to handle money rewards.
     */
    private VaultExtension vaultExtension;

    /**
     * The database connection pool manager.
     */
    private DatabaseManager databaseManager;

    /**
     * The ORMLite connection source.
     */
    private ConnectionSource connectionSource;

    /**
     * The plugin's logger.
     */
    @Inject
    private Logger log;

    /**
     * Utility class for recurring string operations.
     */
    @Inject
    private StringUtils stringUtils;

    /**
     * Utility class to load config files.
     */
    @Inject
    private ConfigUtils configUtils;

    /**
     * Utility class to send formatted messages to a user.
     */
    @Inject
    private MessageUtils messageUtils;

    /**
     * Service class that handles all shop operations.
     */
    @Inject
    private ShopService shopService;

    /**
     * Class that manages every open Shop GUI.
     */
    @Inject
    private ShopGuiManager shopGuiManager;

    /**
     * Task that auto-refreshes the daily shop items periodically (defined in the main config).
     */
    @Inject
    @Named("dailyRefreshTask")
    private Task dailyRefreshTask;

    /**
     * The dispatcher that handles this plugin's commands.
     */
    @Inject
    @Named("lamp")
    private CommandDispatcher commandDispatcher;

    /**
     * Method run on plugin startup. Initializes the main components and registers command classes.
     *
     * @since 1.0
     */
    @Override
    public void onEnable() {
        createDataFolder();
        setupLogging();
        try {
            setupDatabase();
        } catch (SQLException e) {
            log.error("Could not load database! Disabling plugin...", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupInjection();
        loadConfigs();
        printStartupBanner();
        loadExtensions();
        loadShop();
        loadTasks();
        registerCommands();
        log.info("ProjectNozomi is enabled!");
    }

    /**
     * Method run on plugin shutdown. Disables the auto-refresh task (if enabled) and closes all DB connections.
     *
     * @since 1.0
     */
    @Override
    public void onDisable() {
        dailyRefreshTask.stop();
        try {
            databaseManager.close();
        } catch (SQLException e) {
            log.error("Error while closing database connection!", e);
        }
        log.info("ProjectNozomi is disabled!");
    }

    /**
     * Method run upon executing the reload command.
     *
     * @since 1.0
     */
    public void reloadPlugin() {
        reloadConfig();
        loadConfigs();
        loadShop();
        loadTasks();
        log.info("Reloaded plugin");
    }

    private void createDataFolder() {
        var dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new RuntimeIOException("Could not create data folder!");
        }
    }

    private void setupLogging() {
        BukkitLoggerFactory.provideBukkitLogger(this.getLogger());
    }

    private void setupDatabase() throws SQLException {
        var dbPath = getDataFolder().getAbsolutePath() + "/nozomi.db";
        databaseManager = new DatabaseManager("jdbc:sqlite:" + dbPath);
        connectionSource = databaseManager.getConnectionSource();
    }

    private void setupInjection() {
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
