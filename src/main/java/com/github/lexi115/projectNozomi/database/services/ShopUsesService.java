package com.github.lexi115.projectNozomi.database.services;

import com.github.lexi115.projectNozomi.database.entities.ShopUses;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@Singleton
public class ShopUsesService {

    private final Dao<ShopUses, String> dao;

    @Inject
    public ShopUsesService(final ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, ShopUses.class);
        dao = DaoManager.createDao(connectionSource, ShopUses.class);
    }

    public void add(ShopUses uses) {
        try {
            dao.create(uses);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
