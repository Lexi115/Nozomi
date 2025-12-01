package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.extensions.VaultExtension;
import com.github.lexi115.projectNozomi.shop.RewardGiveException;
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
     * @throws RewardGiveException if reward could not be given due to an error.
     * @since 1.0
     */
    @Override
    public void give(final @NonNull Player player, final @NonNull Map<String, String> placeholders) {
        if (!vault.isEnabled() || !vault.deposit(player, amount)) {
            throw new RewardGiveException("Could not give money reward to " + player.getName());
        }
    }
}
