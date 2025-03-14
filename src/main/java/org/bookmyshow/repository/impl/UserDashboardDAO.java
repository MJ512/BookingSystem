package org.bookmyshow.repository.impl;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDashboardDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UserDashboardDAO implements UserDashboardDAOInterface {

    private static final Logger logger = Logger.getLogger(UserDashboardDAO.class.getName());

    @Override
    public final List<Booking> getUserBookingHistory(final int userId) {
        final List<Booking> bookingHistory = new ArrayList<>();
        final String query = """
                SELECT b.*, bs.show_seat_id
                FROM booking b
                LEFT JOIN booked_seats bs ON b.id = bs.booking_id
                WHERE b.user_id = ?
                """;

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Integer, Booking> bookingMap = new HashMap<>();

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("id");
                Booking booking = bookingMap.getOrDefault(bookingId, new Booking(
                        bookingId,
                        resultSet.getInt("user_id"),
                        resultSet.getInt("theater_id"),
                        resultSet.getInt("movie_id"),
                        resultSet.getInt("show_id"),
                        resultSet.getInt("screen_id"),
                        new ArrayList<>(),
                        resultSet.getTimestamp("booking_time").toInstant(),
                        resultSet.getBoolean("is_confirmed")
                ));

                int seatId = resultSet.getInt("show_seat_id");
                if (seatId > 0) {
                    booking.getSeatIds().add(seatId);
                }

                bookingMap.put(bookingId, booking);
            }
            bookingHistory.addAll(bookingMap.values());
        } catch (SQLException e) {
            logger.severe("Database error in getUserBookingHistory: " + e.getMessage());
        }
        return bookingHistory;
    }

    @Override
    public final User getUserById(final int userId) {
        final String query = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("password")
                );
            }
            return null;

        } catch (SQLException e) {
            logger.severe("Database error while fetching user: " + e.getMessage());
            throw new RuntimeException("Error fetching user", e);
        }
    }

    @Override
    public final boolean updateUser(final int userId, final User user) {
        final String query = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setInt(4, userId);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.severe("Database error in updateUser: " + e.getMessage());
            return false;
        }
    }

    @Override
    public final boolean updatePassword(final int userId, final String hashedPassword) {
        final String query = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.severe("Database error in updatePassword: " + e.getMessage());
            return false;
        }
    }
}
