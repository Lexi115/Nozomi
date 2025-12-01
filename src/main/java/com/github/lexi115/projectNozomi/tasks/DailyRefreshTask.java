package com.github.lexi115.projectNozomi.tasks;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.MessageUtils;
import com.github.lexi115.projectNozomi.shop.ShopService;
import com.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
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
     * The message utility class.
     */
    private final MessageUtils messageUtils;

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
     * @param messageUtils The message utility class.
     * @param log The plugin's logger
     * @since 1.0
     */
    @Inject
    public DailyRefreshTask(
            final ProjectNozomi plugin,
            final ShopService shopService,
            final ShopGuiManager shopGuiManager,
            final MessageUtils messageUtils,
            final Logger log
    ) {
        this.plugin = plugin;
        this.shopService = shopService;
        this.shopGuiManager = shopGuiManager;
        this.messageUtils = messageUtils;
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
                log.info("[Daily Refresh Task] Daily items have been refreshed");
                if (plugin.getConfig().getBoolean("daily-items.announce-refresh-in-chat")) {
                    Bukkit.broadcastMessage(messageUtils.getPrefix() + messageUtils.get("info.shop-refreshed"));
                }
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
