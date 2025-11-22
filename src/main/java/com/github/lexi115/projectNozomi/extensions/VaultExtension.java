package com.github.lexi115.projectNozomi.extensions;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

@Singleton
public class VaultExtension implements PluginExtension {

    private final ProjectNozomi plugin;

    private Economy economy;

    @Inject
    public VaultExtension(final ProjectNozomi plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        try {
            var rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public boolean deposit(final OfflinePlayer player, final double amount) {
        var transaction = economy.depositPlayer(player, amount);
        return transaction.transactionSuccess();
    }

    public boolean withdraw(final OfflinePlayer player, final double amount) {
        var transaction = economy.withdrawPlayer(player, amount);
        return transaction.transactionSuccess();
    }

    public double getBalance(final OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public boolean isEnabled() {
        return economy != null;
    }
}
