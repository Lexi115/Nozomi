package com.github.lexi115.projectNozomi.database;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.SQLException;

public class DatabaseManager implements AutoCloseable {

    private final HikariDataSource dataSource;

    @Getter
    private final ConnectionSource connectionSource;

    public DatabaseManager(final String url) throws SQLException {
        this(url, null, null);
    }

    public DatabaseManager(final String url, final String username, final String password) throws SQLException {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        if (username != null) {
            config.setUsername(username);
        }
        if (password != null) {
            config.setPassword(password);
        }
        dataSource = new HikariDataSource(config);
        connectionSource = new DataSourceConnectionSource(dataSource, url);
    }

    @Override
    public void close() throws SQLException {
        try {
            if (connectionSource != null) {
                connectionSource.closeQuietly();
            }
            if (dataSource != null && !dataSource.isClosed()) {
                dataSource.close();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
