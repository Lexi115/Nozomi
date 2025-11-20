package io.github.lexi115.projectNozomi.shop.gui;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopGuiDetails {

    private final String title;

    private final Integer guiSize;

    private final Integer pageSize;

    private final Integer lastAvailableSlot;

    private final GuiElement previousPage;

    private final GuiElement nextPage;

    private final GuiElement currentPage;

    private final Integer[] itemSlots;
}
