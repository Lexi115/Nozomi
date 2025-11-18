package io.github.lexi115.projectNozomi.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ShopItem {
    private final String id;
    private final String name;
    private final Integer amount;
    private final List<Reward> rewards;
}
