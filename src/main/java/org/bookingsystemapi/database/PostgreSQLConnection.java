package org.bookingsystemapi.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQLConnection {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/bookingsystemdb");
        config.setUsername("postgres");
        config.setPassword("Shiffmj1305");
        config.setDriverClassName("org.postgresql.Driver");

        // Pool Settings
        config.setMaximumPoolSize(10); // Max 10 connections
        config.setMinimumIdle(2); // Minimum idle connections
        config.setIdleTimeout(30000); // 30 seconds idle timeout
        config.setConnectionTimeout(30000); // 30 seconds connection timeout
        config.setMaxLifetime(600000); // 10 minutes max connection lifetime

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

