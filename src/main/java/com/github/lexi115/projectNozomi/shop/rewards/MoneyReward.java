package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.extensions.VaultExtension;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

@AllArgsConstructor
public class MoneyReward implements Reward {

    private final VaultExtension vault;

    private final double amount;

    @Override
    public boolean give(final @NonNull Player player, final Map<String, String> placeholders) {
        return vault.isEnabled() && vault.deposit(player, amount);
    }
}
