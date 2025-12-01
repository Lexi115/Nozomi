package com.github.lexi115.projectNozomi.tasks;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

/**
 * A task that auto-refreshes the daily items in the shop periodically (period is defined through a cron expression
 * stored in the <code>config.yml</code> file).
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class DailyRefreshTask implements Task {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The shop service.
     */
    private final ShopService shopService;

    /**
     * The shop GUI manager.
     */
    private final ShopGuiManager shopGuiManager;

    /**
     * The plugin's logger.
     */
    private final Logger log;

    /**
     * The actual cron task that will be executed.
     */
    private BukkitCronTask cronTask;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param shopService The shop service.
     * @param shopGuiManager The shop GUI manager.
     * @param log The plugin's logger
     * @since 1.0
     */
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

    /**
     * Starts the task.
     *
     * @since 1.0
     */
    @Override
    public void start() {
        cronTask = new BukkitCronTask(plugin, log, getCronExpression()) {
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

    /**
     * Stops the task.
     *
     * @since 1.0
     */
    @Override
    public void stop() {
        if (cronTask != null) {
            cronTask.cancel();
        }
    }

    /**
     * Restarts the task (if it's already started).
     *
     * @since 1.0
     */
    @Override
    public void restart() {
        stop();
        start();
    }

    private String getCronExpression() {
        return plugin.getConfig().getString("daily-items.auto-refresh.refresh-time", "0 0 0 ? * *");
    }
}
