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

@Singleton
public class ShopUsesService {

    private final Logger log;

    private final Dao<ShopUses, String> dao;

    private final Shop shop;

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
