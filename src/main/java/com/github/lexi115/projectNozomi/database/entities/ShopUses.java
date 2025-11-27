package com.github.lexi115.projectNozomi.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DatabaseTable(tableName = "shop_uses")
@NoArgsConstructor
@Getter
@Setter
public class ShopUses {

    @DatabaseField(id = true)
    private String playerUuid;

    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int uses;

    @DatabaseField(version = true)
    private int version;

    public ShopUses(final String playerUuid, final int uses) {
        this.playerUuid = playerUuid;
        this.uses = uses;
    }
}
