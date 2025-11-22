package com.github.lexi115.projectNozomi.tasks;

import com.github.lexi115.projectNozomi.shop.ShopService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import lombok.NoArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Singleton
@NoArgsConstructor
public class DailyRefreshTask implements Task {

    private final String TRIGGER_NAME = "daily-refresh-task-trigger";

    private ProjectNozomi plugin;

    private ShopService shopService;

    private ShopGuiManager shopGuiManager;

    private Scheduler scheduler;

    private Logger log;

    @Inject
    public DailyRefreshTask(
            final ProjectNozomi plugin,
            final ShopService shopService,
            final ShopGuiManager shopGuiManager,
            final Logger log
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.shopGuiManager = shopGuiManager;
        this.log = log;
    }

    public void start() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            var dailyRefreshJob = newJob(getClass())
                    .withIdentity("daily-refresh-task")
                    .build();
            var dataMap = dailyRefreshJob.getJobDataMap();
            dataMap.put("plugin", plugin);
            dataMap.put("shopService", shopService);
            dataMap.put("shopGuiManager", shopGuiManager);
            dataMap.put("log", log);
            var cronTrigger = newTrigger()
                    .withIdentity(TRIGGER_NAME)
                    .withSchedule(cronSchedule(getCronExpression()))
                    .build();
            scheduler.scheduleJob(dailyRefreshJob, cronTrigger);
        } catch (SchedulerException e) {
            throw new TaskException(e);
        }
    }

    public void stop() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            throw new TaskException(e);
        }
    }

    public void restart() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.unscheduleJob(new TriggerKey(TRIGGER_NAME));
            }
            start();
        } catch (SchedulerException e) {
            throw new TaskException(e);
        }
    }

    @Override
    public void execute(final JobExecutionContext context) {
        var dataMap = context.getJobDetail().getJobDataMap();
        var plugin = (ProjectNozomi) dataMap.get("plugin");
        var shopService = (ShopService) dataMap.get("shopService");
        var shopGuiManager = (ShopGuiManager) dataMap.get("shopGuiManager");
        var log = (Logger) dataMap.get("log");
        new BukkitRunnable() {
            @Override
            public void run() {
                shopGuiManager.closeAll();
            }
        }.runTask(plugin);
        shopService.refreshDailyItems();
        shopService.saveDailyItemsInConfig();
        log.info("[TIMER] Refreshed items");
    }

    private String getCronExpression() {
        return plugin.getConfig().getString("daily-items.auto-refresh.refresh-time", "0 0 0 ? * *");
    }
}
