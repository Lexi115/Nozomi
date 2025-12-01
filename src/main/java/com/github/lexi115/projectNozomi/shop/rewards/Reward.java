package com.github.lexi115.projectNozomi.shop.rewards;

import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Interface for a generic reward.
 *
 * @author Lexi115
 * @since 1.0
 */
public interface Reward {

    /**
     * Gives the reward to the specified player.
     *
     * @param player       The target player
     * @param placeholders The placeholders map.
     * @since 1.0
     */
    void give(@NonNull Player player, @NonNull Map<String, String> placeholders);
}
