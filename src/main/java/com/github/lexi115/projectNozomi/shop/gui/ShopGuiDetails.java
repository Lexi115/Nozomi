package com.github.lexi115.projectNozomi.shop.gui;

import lombok.Builder;
import lombok.Getter;

/**
 * Information about the Shop GUI itself.
 *
 * @author Lexi115
 * @since 1.0
 */
@Getter
@Builder
public class ShopGuiDetails {

    /**
     * The GUI title (the name of the inventory).
     */
    private final String title;

    /**
     * The size of the inventory (must be a multiple of 9, in the 9-54 range).
     */
    private final int guiSize;

    /**
     * The size of the inventory excluding the last row reserved for UI elements. It can vary depending on whether
     * a custom slot layout was specified in the <code>shop.yml</code> config file (<code>item-slots</code>).
     */
    private final int pageSize;

    /**
     * The last available slot where a shop item can be put in (for that page).
     */
    private final int lastAvailableSlot;

    /**
     * The previous page UI element.
     */
    private final GuiElement previousPage;

    /**
     * The next page UI element.
     */
    private final GuiElement nextPage;

    /**
     * The current page UI element.
     */
    private final GuiElement currentPage;

    /**
     * The custom item slot layout (if specified in the <code>shop.yml</code> config file).
     */
    private final Integer[] itemSlots;
}
