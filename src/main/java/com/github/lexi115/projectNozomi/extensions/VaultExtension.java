package com.github.lexi115.projectNozomi.extensions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Vault API plugin extension.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class VaultExtension implements PluginExtension {

    /**
     * The plugin instance.
     */
    private final JavaPlugin plugin;

    /**
     * The economy API.
     */
    private Economy economy;

    /**
     * Constructor.
     *
     * @param plugin The plugin instance.
     * @since 1.0
     */
    @Inject
    public VaultExtension(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets up and loads the extension.
     *
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @since 1.0
     */
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

    /**
     * Checks whether the extension is properly loaded and enabled.
     *
     * @return <code>true</code> if the extension is enabled, <code>false</code> otherwise.
     * @since 1.0
     */
    public boolean isEnabled() {
        return economy != null;
    }

    /**
     * Deposits a certain amount of money into the specified player's account.
     *
     * @param player The target player.
     * @param amount The amount to deposit.
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @throws IllegalArgumentException If specified amount is negative.
     * @since 1.0
     */
    public boolean deposit(final @NonNull OfflinePlayer player, final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be a positive number!");
        }
        var transaction = economy.depositPlayer(player, amount);
        return transaction.transactionSuccess();
    }

    /**
     * Withdraws a certain amount of money from the specified player's account.
     *
     * @param player The target player.
     * @param amount The amount to withdraw.
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @throws IllegalArgumentException If specified amount is negative.
     * @since 1.0
     */
    public boolean withdraw(final @NonNull OfflinePlayer player, final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount should be a positive number!");
        }
        var transaction = economy.withdrawPlayer(player, amount);
        return transaction.transactionSuccess();
    }

    /**
     * Returns the specified player's balance.
     *
     * @param player The target player.
     * @return The corresponding balance.
     * @since 1.0
     */
    public double getBalance(final @NonNull OfflinePlayer player) {
        return economy.getBalance(player);
    }
}
