package io.github.lexi115.projectNozomi.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.lexi115.projectNozomi.ProjectNozomi;
import io.github.lexi115.projectNozomi.shop.DailyItemRefreshTask;
import io.github.lexi115.projectNozomi.shop.Shop;
import io.github.lexi115.projectNozomi.shop.ShopService;
import io.github.lexi115.projectNozomi.shop.gui.ItemMapper;
import io.github.lexi115.projectNozomi.shop.gui.ShopGuiManager;
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
        bind(Shop.class).asEagerSingleton();
        bind(ItemMapper.class).asEagerSingleton();
        bind(ShopService.class).asEagerSingleton();
        bind(ShopGuiManager.class).asEagerSingleton();
        bind(DailyItemRefreshTask.class).asEagerSingleton();
    }
}
