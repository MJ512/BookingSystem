package org.bookmyshow.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bookmyshow.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the HikariCP connection pool for PostgreSQL.
 * Pool settings are loaded from config.properties.
 */
public final class PostgreSQLConnection {

    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLConnection.class);
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getProperty("db.url"));
        config.setUsername(ConfigLoader.getProperty("db.username"));
        config.setPassword(ConfigLoader.getProperty("db.password"));
        config.setDriverClassName(ConfigLoader.getProperty("db.driver"));
        config.setMaximumPoolSize(Integer.parseInt(ConfigLoader.getProperty("db.pool.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(ConfigLoader.getProperty("db.pool.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(ConfigLoader.getProperty("db.pool.idleTimeout", "30000")));
        config.setConnectionTimeout(Long.parseLong(ConfigLoader.getProperty("db.pool.connectionTimeout", "30000")));
        config.setMaxLifetime(Long.parseLong(ConfigLoader.getProperty("db.pool.maxLifetime", "600000")));

        try {
            dataSource = new HikariDataSource(config);
            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool.", e);
            throw new ExceptionInInitializerError("Database pool initialization failed.");
        }
    }

    private PostgreSQLConnection() {
        // Utility class – no instantiation
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed.");
        }
    }
}
