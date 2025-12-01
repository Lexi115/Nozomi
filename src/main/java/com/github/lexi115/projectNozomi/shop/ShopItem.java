package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.shop.rewards.Reward;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An item that can be sold in the shop.
 *
 * @author Lexi115
 * @since 1.0
 */
@Getter
@SuperBuilder
public class ShopItem extends Item {

    /**
     * The amount required to sell this item.
     */
    private final int amount;

    /**
     * The list of rewards given to the player upon successfully selling this item.
     */
    @Builder.Default
    private final List<Reward> rewards = new ArrayList<>();
}
