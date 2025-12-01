package com.github.lexi115.projectNozomi.shop;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
public abstract class Item {

    /**
     * The id of this item.
     */
    private final String id;

    /**
     * The name of this item.
     */
    private final String name;

    /**
     * The display name of this item.
     */
    private final String displayName;

    /**
     * The material representation of this item.
     */
    private final Material material;

    /**
     * The lore of this item.
     */
    @Builder.Default
    private final List<String> lore = new ArrayList<>();
}
