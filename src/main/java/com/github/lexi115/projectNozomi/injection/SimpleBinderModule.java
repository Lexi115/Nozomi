package com.github.lexi115.projectNozomi.injection;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.commands.CommandDispatcher;
import com.github.lexi115.projectNozomi.commands.LampCommandDispatcher;
import com.github.lexi115.projectNozomi.tasks.DailyRefreshTask;
import com.github.lexi115.projectNozomi.tasks.Task;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dependency injection class binder module.
 *
 * @author Lexi115
 * @since 1.0
 */
public class SimpleBinderModule extends AbstractModule {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @since 1.0
     */
    public SimpleBinderModule(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates the dependency injector.
     *
     * @return The injector.
     * @since 1.0
     */
    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    /**
     * Binds interfaces and instances to their respective implementations.
     *
     * @since 1.0
     */
    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
        bind(ProjectNozomi.class).toInstance(plugin);
        bind(Logger.class).toInstance(LoggerFactory.getLogger(plugin.getName()));
        bind(ConnectionSource.class).toInstance(plugin.getConnectionSource());
        bind(Task.class).annotatedWith(Names.named("dailyRefreshTask")).to(DailyRefreshTask.class);
        bind(CommandDispatcher.class).annotatedWith(Names.named("lamp")).to(LampCommandDispatcher.class);
    }
}
