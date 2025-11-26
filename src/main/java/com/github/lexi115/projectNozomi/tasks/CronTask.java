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

public abstract class CronTask implements Runnable {

    private final JavaPlugin plugin;

    private final Logger log;

    private final ExecutionTime executionTime;

    private BukkitTask currentTask;

    public CronTask(final JavaPlugin plugin, final Logger log, final String cronExpression) {
        this.plugin = plugin;
        this.log = log;
        var parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
        var cron = parser.parse(cronExpression);
        this.executionTime = ExecutionTime.forCron(cron);
    }

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

    public void cancel() {
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
        }
    }
}
