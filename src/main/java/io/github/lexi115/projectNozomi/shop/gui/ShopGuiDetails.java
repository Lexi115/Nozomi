package io.github.lexi115.projectNozomi.shop.gui;

import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Builder
public class ShopGuiDetails {

    private final String title;

    private final Integer size;

    private final GuiElement previousPage;

    private final GuiElement nextPage;

    private final GuiElement currentPage;

    @Builder.Default
    private final Set<Integer> itemSlots = new LinkedHashSet<>();
}
