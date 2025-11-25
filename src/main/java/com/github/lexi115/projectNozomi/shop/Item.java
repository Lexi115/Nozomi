package com.github.lexi115.projectNozomi.shop;

import org.bukkit.Material;

import java.util.List;

public interface Item {
    String getName();

    String getDisplayName();
    Material getMaterial();

    List<String> getLore();
}
