package io.github.lexi115.projectNozomi;

import io.github.lexi115.projectNozomi.commands.PluginCommands;
import io.github.lexi115.projectNozomi.commands.ShopCommands;
import io.github.lexi115.projectNozomi.shop.DailyItemRefreshTask;
import io.github.lexi115.projectNozomi.shop.Shop;
import io.github.lexi115.projectNozomi.shop.ShopService;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import revxrsal.commands.bukkit.BukkitLamp;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

@Getter
public final class ProjectNozomi extends JavaPlugin {
    private final Logger log = LoggerFactory.getLogger(ProjectNozomi.class);
    private FileConfiguration shopConfig;
    private FileConfiguration messagesConfig;
    private ShopService shopService;
    private Shop shop;
    private Scheduler scheduler;

    @Override
    public void onEnable() {
        loadConfigs();
        loadShop();
        try {
            setupScheduler();
        } catch (SchedulerException | ParseException e) {
            throw new RuntimeException(e);
        }
        registerCommands();
        log.info("ProjectNozomi is enabled!");
    }

    @Override
    public void onDisable() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
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
            shopService = new ShopService(this, new Shop());
        }
        shop = shopService.loadShopFromConfig(shopConfig);
        log.info("Loaded shop with {} items", shop.getTotalItems());
        shopService.refreshDailyItems();
        log.info("Refreshed daily items");
    }

    private void setupScheduler() throws SchedulerException, ParseException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        var dailyRefreshJob = newJob(DailyItemRefreshTask.class)
                .withIdentity("daily-refresh-task")
                .build();
        var dataMap = dailyRefreshJob.getJobDataMap();
        dataMap.put("plugin", this);
        dataMap.put("shopService", shopService);
//        var trigger = TriggerBuilder.newTrigger()
//                        .withIdentity("daily-refresh-task-trigger")
//                                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever())
//                                        .build();
        var cronTrigger = TriggerBuilder.newTrigger()
                        .withIdentity("daily-refresh-task-trigger", "group1")
                                .withSchedule(cronSchedule("00 05 15 ? * *"))
                                        .build();
        scheduler.scheduleJob(dailyRefreshJob, cronTrigger);
    }

//    private void setupDailyItemRefreshTimer() {
//        var timeRefresh = Optional.ofNullable(
//                getConfig().getString("daily-items.refresh-time")).orElse("00:00:00");
//        var splitTimeRefresh = timeRefresh.split(":");
//        var today = Calendar.getInstance();
//        today.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeRefresh[0]));
//        today.set(Calendar.MINUTE, Integer.parseInt(splitTimeRefresh[1]));
//        today.set(Calendar.SECOND, Integer.parseInt(splitTimeRefresh[2]));
//        var timer = new Timer();
//        timer.schedule(
//                new DailyItemRefreshTask(this, shopService),
//                today.getTime(),
//                TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
//        );
//        log.info("Daily refresh set at {}", timeRefresh);
//    }

    private void registerCommands() {
        var lamp = BukkitLamp.builder(this)
                .dependency(Logger.class, log)
                .dependency(ShopService.class, shopService)
                .build();
        lamp.register(new PluginCommands(this));
        lamp.register(new ShopCommands(this));
    }
}
