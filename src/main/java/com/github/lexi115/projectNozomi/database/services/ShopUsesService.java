package com.github.lexi115.projectNozomi.database.services;

import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;

import java.sql.SQLException;
import java.util.UUID;

@Singleton
public class ShopUsesService {

    private final Logger log;

    private final Dao<ShopUses, String> dao;

    @Inject
    public ShopUsesService(final ConnectionSource connectionSource, final Logger log) throws SQLException {
        this.log = log;
        TableUtils.createTableIfNotExists(connectionSource, ShopUses.class);
        dao = DaoManager.createDao(connectionSource, ShopUses.class);
    }

    public boolean savePlayer(
            final @NonNull String uuid,
            final int uses,
            final @NonNull String refreshId
    ) {
        try {
            int rowsAffected;
            var record = dao.queryForId(uuid);
            if (record == null) {
                record = new ShopUses(uuid, uses, refreshId);
                rowsAffected = dao.create(record);
            } else {
                record.setUses(uses);
                rowsAffected = dao.update(record);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error while saving player uses in database:", e);
            return false;
        }
    }

    public int getPlayerUses(final @NonNull String uuid) {
        try {
            var record = dao.queryForId(uuid);
            if (record == null) {
                return 0;
            }
            return record.getUses();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getPlayerMaxUses(final @NonNull String uuid) {
        var player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player == null) {
            throw new InvalidPlayerException("Player not found");
        }
        return getPlayerMaxUsesFromPermissions(player);
    }

    private int getPlayerMaxUsesFromPermissions(final @NonNull Player player) {
        var playerPermissions = player.getEffectivePermissions();
        String permString;
        var maxUses = ShopUses.UNLIMITED;
        for (var perm : playerPermissions) {
            permString = perm.getPermission();
            if (permString.equals("nozomi.uses.max.unlimited")) {
                return ShopUses.UNLIMITED;
            }
            if (permString.matches("nozomi\\.uses\\.max\\.\\d+")) {
                maxUses = Math.max(maxUses, Integer.parseInt(permString.split("\\.")[3]));
            }
        }
        return maxUses;
    }
}
