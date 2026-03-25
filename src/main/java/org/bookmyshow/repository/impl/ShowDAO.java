package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.repository.ShowDAOInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

@Singleton
public class ShowDAO implements ShowDAOInterface {

    private static final Logger logger = LoggerFactory.getLogger(ShowDAO.class);

    private static final String SELECT_START_TIME = "SELECT start_time FROM movie_show WHERE id = ?";
    private static final String SELECT_SHOW_BY_BOOKING = "SELECT movie_show_id FROM booking WHERE id = ?";

    @Override
    public boolean hasShowStarted(final int showId) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_START_TIME)) {

            ps.setInt(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp startTime = rs.getTimestamp("start_time");
                    return startTime.toInstant().isBefore(Instant.now());
                }
            }
        }
        // If show not found, treat as started to prevent booking
        logger.warn("Show ID {} not found — defaulting hasShowStarted to true.", showId);
        return true;
    }

    @Override
    public int getShowIdByBookingId(final int bookingId) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_SHOW_BY_BOOKING)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("movie_show_id");
                }
            }
        }
        throw new SQLException("No show found for booking ID: " + bookingId);
    }
}
