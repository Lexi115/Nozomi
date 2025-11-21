package com.github.lexi115.projectNozomi.shop;

import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Reward {

    void give(@NonNull Player player, Map<String, String> placeholders);
}
