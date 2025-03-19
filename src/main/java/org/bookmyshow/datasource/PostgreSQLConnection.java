package org.bookmyshow.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bookmyshow.config.ConfigLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnection {
    private static final Logger logger = Logger.getLogger(PostgreSQLConnection.class.getName());
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getProperty("db.url"));
        config.setUsername(ConfigLoader.getProperty("db.username"));
        config.setPassword(ConfigLoader.getProperty("db.password"));
        config.setDriverClassName(ConfigLoader.getProperty("db.driver"));

        // Connection Pool Settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(600000);

        try {
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing HikariCP connection pool", e);
            throw new ExceptionInInitializerError("Failed to initialize database connection pool.");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            if (connection == null || connection.isClosed()) {
                logger.warning("Warning: Retrieved a null or closed connection from the pool.");
            }
            return connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get a database connection.", e);
            throw e;
        }
    }

    public static void close() {
        if (dataSource != null) {
            logger.info("Closing database connection pool...");
            dataSource.close();
            logger.info("Database connection pool closed successfully.");
        }
    }
}
