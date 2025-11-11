package io.github.lexi115.projectNozomi;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("ProjectNozomi is enabled!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("ProjectNozomi is disabled!!");
    }
}
