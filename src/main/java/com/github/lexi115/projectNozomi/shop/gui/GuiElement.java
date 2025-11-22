package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.shop.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
@Builder
public class GuiElement implements Item {

    private String name;

    private Material material;

    private int slot;
}
