package com.github.lexi115.projectNozomi.shop.gui;

import com.github.lexi115.projectNozomi.shop.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class GuiElement implements Item {

    private final String name;

    private final String displayName;

    private final Material material;

    private final int slot;

    @Builder.Default
    private final List<String> lore = new ArrayList<>();
}
