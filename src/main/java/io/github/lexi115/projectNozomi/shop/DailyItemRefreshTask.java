package io.github.lexi115.projectNozomi.shop;

import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class DailyItemRefreshTask implements Job {
    @Override
    public void execute(final JobExecutionContext context) {
        var dataMap = context.getJobDetail().getJobDataMap();
        var plugin = (JavaPlugin) dataMap.get("plugin");
        var shopService = (ShopService) dataMap.get("shopService");
        shopService.refreshDailyItems();
        plugin.getLogger().info("[TIMER] Refreshed items");
    }
}
