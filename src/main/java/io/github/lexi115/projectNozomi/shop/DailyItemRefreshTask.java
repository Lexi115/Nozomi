package io.github.lexi115.projectNozomi.shop;

import lombok.NoArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

@NoArgsConstructor
public class DailyItemRefreshTask implements Job {

    private JavaPlugin plugin;

    private ShopService shopService;

    private Scheduler scheduler;

    private final String TRIGGER_NAME = "daily-refresh-task-trigger";

    public DailyItemRefreshTask(final JavaPlugin plugin, final ShopService shopService) {
        this.plugin = plugin;
        this.shopService = shopService;
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
            var cronTrigger = TriggerBuilder.newTrigger()
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
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new TaskException(e);
        }
    }

    public void restart() {
        try {
            scheduler.unscheduleJob(new TriggerKey(TRIGGER_NAME));
            start();
        } catch (SchedulerException e) {
            throw new TaskException(e);
        }
    }

    @Override
    public void execute(final JobExecutionContext context) {
        var dataMap = context.getJobDetail().getJobDataMap();
        var plugin = (JavaPlugin) dataMap.get("plugin");
        var shopService = (ShopService) dataMap.get("shopService");
        shopService.refreshDailyItems();
        plugin.getLogger().info("[TIMER] Refreshed items");
    }

    private String getCronExpression() {
        return plugin.getConfig().getString("daily-items.refresh-time", "0 0 0 ? * *");
    }
}
