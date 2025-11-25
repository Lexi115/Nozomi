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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleBinderModule extends AbstractModule {

    private final ProjectNozomi plugin;

    public SimpleBinderModule(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        bind(ProjectNozomi.class).toInstance(plugin);
        bind(Logger.class).toInstance(LoggerFactory.getLogger(plugin.getName()));
        bind(Task.class).annotatedWith(Names.named("dailyRefreshTask")).to(DailyRefreshTask.class);
        bind(CommandDispatcher.class).annotatedWith(Names.named("lamp")).to(LampCommandDispatcher.class);
    }
}
