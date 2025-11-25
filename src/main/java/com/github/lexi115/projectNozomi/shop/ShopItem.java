package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.shop.rewards.Reward;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ShopItem implements Item {

    private final String id;

    private final String name;

    private final String displayName;

    private final Material material;

    private final int amount;

    @Builder.Default
    private final List<Reward> rewards = new ArrayList<>();

    @Builder.Default
    private final List<String> lore = new ArrayList<>();
}
