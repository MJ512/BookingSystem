package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class UserDashboardImpl implements UserDashboardRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardImpl.class);

    private static final String SELECT_HISTORY = """
            SELECT b.id, b.user_id, b.movie_show_id, b.booking_time, b.is_confirmed, bs.seat_id
            FROM booking b
            LEFT JOIN booked_seats bs ON b.id = bs.booking_id
            WHERE b.user_id = ?
            ORDER BY b.booking_time DESC
            """;
    private static final String SELECT_USER = "SELECT id, name, email, phone, password FROM users WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";

    @Override
    public List<Booking> getUserBookingHistory(final int userId) {
        // Use LinkedHashMap to preserve insertion (time-ordered) sequence
        Map<Integer, Booking> bookingMap = new LinkedHashMap<>();

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_HISTORY)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int bookingId = rs.getInt("id");
                    bookingMap.computeIfAbsent(bookingId, id -> {
                        try {
                            return new Booking(
                                    rs.getInt("user_id"),
                                    rs.getInt("movie_show_id"),
                                    new ArrayList<>(),
                                    rs.getBoolean("is_confirmed")
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    int seatId = rs.getInt("seat_id");
                    if (!rs.wasNull()) {
                        bookingMap.get(bookingId).getSeatIds().add(seatId);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("DB error fetching booking history for user {}.", userId, e);
        }
        return new ArrayList<>(bookingMap.values());
    }

    @Override
    public User getUserById(final int userId) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("name"),
                            rs.getString("email"), rs.getString("phone"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            logger.error("DB error fetching user {}.", userId, e);
            throw new RuntimeException("Error fetching user.", e);
        }
        return null;
    }

    @Override
    public boolean updateUser(final int userId, final User user) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("DB error updating user {}.", userId, e);
            return false;
        }
    }

    @Override
    public boolean updatePassword(final int userId, final String hashedPassword) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD)) {

            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("DB error updating password for user {}.", userId, e);
            return false;
        }
    }
}
