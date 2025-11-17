package io.github.lexi115.projectNozomi.shop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ShopItem {
    private String name;
    private Integer amount;
    private List<Reward> rewards;
}
