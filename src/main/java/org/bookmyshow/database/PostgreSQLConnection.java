package org.bookmyshow.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnection {
    private static final Logger logger = Logger.getLogger(PostgreSQLConnection.class.getName());
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/bookingsystemdb");
        config.setUsername("postgres");
        config.setPassword("Shiffmj1305");
        config.setDriverClassName("org.postgresql.Driver");

        // Connection Pool Settings
        config.setMaximumPoolSize(10);  // Maximum number of connections
        config.setMinimumIdle(2);       // Minimum number of idle connections
        config.setIdleTimeout(30000);   // 30 seconds idle timeout
        config.setConnectionTimeout(30000); // 30 seconds connection timeout
        config.setMaxLifetime(600000);  // 10 minutes max connection lifetime

        try {
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing HikariCP connection pool", e);
            throw new ExceptionInInitializerError("Failed to initialize database connection pool.");
        }
    }

    // Get a connection from the pool
    public static final Connection getConnection() throws SQLException {
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

    // Close the connection pool safely
    public static final void close() {
        if (dataSource != null) {
            logger.info("Closing database connection pool...");
            dataSource.close();
            logger.info("Database connection pool closed successfully.");
        }
    }
}
