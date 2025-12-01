package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.shop.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * An interactable GUI (inventory) element, often used for page navigation buttons.
 *
 * @author Lexi115
 * @since 1.0
 */
@Getter
@Setter
@SuperBuilder
public class GuiElement extends Item {

    /**
     * The inventory slot that will be occupied by the element.
     */
    private final int slot;
}
