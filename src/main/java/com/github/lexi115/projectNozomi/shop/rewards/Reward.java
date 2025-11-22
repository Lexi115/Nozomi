package com.github.lexi115.projectNozomi.shop.rewards;

import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Reward {

    boolean give(@NonNull Player player, Map<String, String> placeholders);
}
