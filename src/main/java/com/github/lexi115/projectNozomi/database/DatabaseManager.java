package com.github.lexi115.projectNozomi.database;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.SQLException;

/**
 * Class that manages a database connection pool.
 *
 * @author Lexi115
 * @since 1.0
 */
public class DatabaseManager implements AutoCloseable {

    /**
     * The data source.
     */
    private final HikariDataSource dataSource;

    /**
     * The ORMLite connection source.
     */
    @Getter
    private final ConnectionSource connectionSource;

    /**
     * Constructor.
     *
     * @param url The URL used to connect to the database.
     * @throws SQLException if things go wrong when trying to establish the database connection.
     * @since 1.0
     */
    public DatabaseManager(final String url) throws SQLException {
        this(url, null, null);
    }

    /**
     * Constructor.
     *
     * @param url The URL used to connect to the database.
     * @param username The username.
     * @param password The user password.
     * @throws SQLException if things go wrong when trying to establish the database connection.
     * @since 1.0
     */
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

    /**
     * Closes the connection pool.
     *
     * @throws SQLException if an error occurs while closing the pool.
     * @since 1.0
     */
    @Override
    public void close() throws SQLException {
        if (connectionSource != null) {
            connectionSource.closeQuietly();
        }
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
