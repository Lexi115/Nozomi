package com.github.lexi115.projectNozomi.tasks;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

@Singleton
public class DailyRefreshTask implements Task {

    private final ProjectNozomi plugin;

    private final ShopService shopService;

    private final ShopGuiManager shopGuiManager;

    private final Logger log;

    private CronTask cronTask;

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

    @Override
    public void start() {
        cronTask = new CronTask(plugin, log, getCronExpression()) {
            @Override
            public void run() {
                shopGuiManager.closeAll();
                shopService.refreshDailyItems();
                shopService.saveDailyItemsInConfig();
                log.info("[TIMER] Refreshed items");
            }
        };
        cronTask.schedule();
    }

    @Override
    public void stop() {
        if (cronTask != null) {
            cronTask.cancel();
        }
    }

    @Override
    public void restart() {
        stop();
        start();
    }

    private String getCronExpression() {
        return plugin.getConfig().getString("daily-items.auto-refresh.refresh-time", "0 0 0 ? * *");
    }
}
