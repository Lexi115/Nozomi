package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.extensions.VaultExtension;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Reward that consists of giving money to a player.
 *
 * @author Lexi115
 * @since 1.0
 */
@AllArgsConstructor
public class MoneyReward implements Reward {

    /**
     * The Vault API plugin extension, used for depositing money into the player's account.
     */
    private final VaultExtension vault;

    /**
     * The amount of money to give.
     */
    private final double amount;

    /**
     * Gives the reward to the specified player.
     *
     * @param player       The target player
     * @param placeholders The placeholders map.
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise.
     * @since 1.0
     */
    @Override
    public boolean give(final @NonNull Player player, final Map<String, String> placeholders) {
        return vault.isEnabled() && vault.deposit(player, amount);
    }
}
