package io.github.lexi115.projectNozomi.shop.gui;

import io.github.lexi115.projectNozomi.shop.Item;
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

    private Integer slot;
}
