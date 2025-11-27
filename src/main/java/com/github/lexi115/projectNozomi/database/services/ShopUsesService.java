package com.github.lexi115.projectNozomi.database.services;

import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.UUID;

@Singleton
public class ShopUsesService {

    private final Dao<ShopUses, String> dao;

    @Inject
    public ShopUsesService(final ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, ShopUses.class);
        dao = DaoManager.createDao(connectionSource, ShopUses.class);
    }

    public boolean savePlayer(final @NonNull UUID uuid, final int uses) {
        try {
            int rowsAffected;
            ShopUses record = dao.queryForId(uuid.toString());
            if (record == null) {
                record = new ShopUses(uuid.toString(), uses);
                rowsAffected = dao.create(record);
            } else {
                record.setUses(uses);
                rowsAffected = dao.update(record);
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
