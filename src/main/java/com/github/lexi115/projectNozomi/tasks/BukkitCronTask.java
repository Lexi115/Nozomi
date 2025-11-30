package com.github.lexi115.projectNozomi.tasks;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A Bukkit synchronous task that is scheduled based on a cron expression. Runs on the server's main thread.
 *
 * @author Lexi115
 * @since 1.0
 */
public abstract class BukkitCronTask implements Runnable {

    /**
     * The plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * The plugin's logger.
     */
    private final Logger log;

    /**
     * The execution time.
     */
    private final ExecutionTime executionTime;

    /**
     * The currently scheduled task.
     */
    private BukkitTask currentTask;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @param log The plugin's logger.
     * @param cronExpression The cron expression.
     * @since 1.0
     */
    public BukkitCronTask(final JavaPlugin plugin, final Logger log, final String cronExpression) {
        this.plugin = plugin;
        this.log = log;
        var parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        var cron = parser.parse(cronExpression);
        this.executionTime = ExecutionTime.forCron(cron);
    }

    /**
     * Schedules the task.
     *
     * @since 1.0
     */
    public void schedule() {
        var now = ZonedDateTime.now();
        var nextExecution = executionTime.nextExecution(now);
        if (nextExecution.isPresent()) {
            long delayMillis = ChronoUnit.MILLIS.between(now, nextExecution.get());
            if (delayMillis < 0) {
                delayMillis = 0;
            }
            long delayTicks = (long) Math.ceil(delayMillis / 50.0);
            this.currentTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    this.run();
                } catch (Exception e) {
                    log.error("Error during task:", e);
                } finally {
                    schedule();
                }
            }, delayTicks);
        }
    }

    /**
     * Cancels the task if it's still running.
     *
     * @since 1.0
     */
    public void cancel() {
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
        }
    }
}
