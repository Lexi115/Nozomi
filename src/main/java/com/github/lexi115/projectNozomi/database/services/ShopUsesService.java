package com.github.lexi115.projectNozomi.database.services;

import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.github.lexi115.projectNozomi.shop.Shop;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.sql.SQLException;

/**
 * Service class that keeps track of players' shop uses, interacting directly with the database.
 */
@Singleton
public class ShopUsesService {

    /**
     * The plugin's logger.
     */
    private final Logger log;

    /**
     * DAO used to interact with the <code>shop_uses</code> database table.
     */
    private final Dao<ShopUses, String> dao;

    /**
     * The shop.
     */
    private final Shop shop;

    /**
     * Constructor.
     *
     * @param connectionSource The ORMLite connection source.
     * @param log The plugin's logger.
     * @param shop The shop.
     * @throws SQLException if things go wrong while creating the related table or DAO.
     * @since 1.0
     */
    @Inject
    public ShopUsesService(
            final ConnectionSource connectionSource,
            final Logger log,
            final Shop shop
    ) throws SQLException {
        this.log = log;
        this.shop = shop;
        TableUtils.createTableIfNotExists(connectionSource, ShopUses.class);
        this.dao = DaoManager.createDao(connectionSource, ShopUses.class);
    }

    /**
     * Saves an amount of shop uses for a certain player. If the entry already exists, it will be updated instead.
     *
     * @param player The target player.
     * @param uses The amount of uses.
     * @param refreshId The shop's <code>refreshId</code>.
     * @return the saved {@link ShopUses} record object.
     * @since 1.0
     */
    public ShopUses savePlayerUses(
            final @NonNull Player player,
            final int uses,
            final @NonNull String refreshId
    ) {
        try {
            var uuid = player.getUniqueId().toString();
            int rowsAffected;
            var record = dao.queryForId(uuid);
            if (record == null) {
                record = new ShopUses(uuid, uses, refreshId);
                rowsAffected = dao.create(record);
            } else {
                record.setUses(uses);
                record.setRefreshId(refreshId);
                rowsAffected = dao.update(record);
            }
            if (rowsAffected == 0) {
                throw new SQLException("Race condition");
            }
            return record;
        } catch (SQLException e) {
            log.error("Error while saving player uses in database:", e);
            return null;
        }
    }

    /**
     * Returns the amount of shop uses left for a certain player. If it can't find the entry or the
     * <code>refreshId</code> doesn't match the shop's current <code>refreshId</code>, it will generate a new
     * one, while setting the maximum amount of shop uses that player can have (based on his permissions).
     *
     * @param player The target player.
     * @return The amount of shop uses left for that player.
     * @since 1.0
     */
    public int getPlayerUses(final @NonNull Player player) {
        try {
            var uuid = player.getUniqueId().toString();
            var record = dao.queryForId(uuid);
            var shopRefreshId = shop.getRefreshId();
            if (record == null || !record.getRefreshId().equals(shopRefreshId)) {
                record = savePlayerUses(player, getPlayerMaxUses(player), shopRefreshId);
            }
            return record.getUses();
        } catch (SQLException | NullPointerException e) {
            return 0;
        }
    }

    /**
     * Returns the maximum amount of shop uses a player can have (based on his permissions). If the player has the
     * <code>nozomi.uses.max.unlimited</code> permission, it will return -1.
     *
     * @param player The target player.
     * @return The maximum amount of shop uses for that player, or -1 if he has unlimited uses.
     * @since 1.0
     */
    public int getPlayerMaxUses(final @NonNull Player player) {
        var playerPermissions = player.getEffectivePermissions();
        String permString;
        var maxUses = ShopUses.UNLIMITED;
        for (var perm : playerPermissions) {
            permString = perm.getPermission();
            if (permString.equals("nozomi.uses.max.unlimited") && perm.getValue()) {
                return ShopUses.UNLIMITED;
            }
            if (permString.matches("nozomi\\.uses\\.max\\.\\d+") && perm.getValue()) {
                maxUses = Math.max(maxUses, Integer.parseInt(permString.split("\\.")[3]));
            }
        }
        return maxUses;
    }
}
