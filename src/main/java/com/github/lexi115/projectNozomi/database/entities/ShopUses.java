package com.github.lexi115.projectNozomi.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Shop uses entity.
 *
 * @author Lexi115
 * @since 1.0
 */
@DatabaseTable(tableName = "shop_uses")
@NoArgsConstructor
@Getter
@Setter
public class ShopUses {

    /**
     * Constant that implies unlimited shop uses.
     */
    public static final int UNLIMITED = -1;

    /**
     * The player's UUID.
     */
    @DatabaseField(id = true)
    private String playerUuid;

    /**
     * The amount of shop uses left.
     */
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int uses;

    /**
     * The shop's <code>refreshId</code> at the moment of last usage.
     */
    @DatabaseField(canBeNull = false)
    private String refreshId;

    /**
     * The database record version.
     */
    @DatabaseField(version = true)
    private int version;

    /**
     * Constructor.
     *
     * @param playerUuid The player's UUID.
     * @param uses       The amount of shop uses left.
     * @param refreshId  The shop's <code>refreshId</code> at the moment of last usage.
     * @since 1.0
     */
    public ShopUses(final String playerUuid, final int uses, final String refreshId) {
        this.playerUuid = playerUuid;
        this.uses = uses;
        this.refreshId = refreshId;
    }
}
