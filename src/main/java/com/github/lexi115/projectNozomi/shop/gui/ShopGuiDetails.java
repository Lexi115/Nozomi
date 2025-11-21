package com.github.lexi115.projectNozomi.shop.gui;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopGuiDetails {

    private final String title;

    private final int guiSize;

    private final int pageSize;

    private final int lastAvailableSlot;

    private final GuiElement previousPage;

    private final GuiElement nextPage;

    private final GuiElement currentPage;

    private final Integer[] itemSlots;
}
