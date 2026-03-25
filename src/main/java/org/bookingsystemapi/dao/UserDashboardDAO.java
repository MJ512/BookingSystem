package org.bookingsystemapi.dao;

import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardDAO {
    public List<Booking> getUserBookingHistory(int userId) {
        final List<Booking> bookingHistory = new ArrayList<>();
        final String bookingQuery = "SELECT * FROM bookings WHERE user_id = ?";
        final String seatQuery = "SELECT show_seat_id FROM booking_seats WHERE booking_id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement bookingStmt = connection.prepareStatement(bookingQuery)) {
            bookingStmt.setInt(1, userId);
            ResultSet bookingResult = bookingStmt.executeQuery();

            while (bookingResult.next()) {
                Booking booking = new Booking();
                booking.setId(bookingResult.getInt("id"));
                booking.setUserId(bookingResult.getInt("user_id"));
                booking.setTheaterId(bookingResult.getInt("theater_id"));
                booking.setMovieId(bookingResult.getInt("movie_id"));
                booking.setShowId(bookingResult.getInt("show_id"));
                booking.setScreenId(bookingResult.getInt("screen_id"));
                booking.setBookingTime(bookingResult.getTimestamp("booking_time").toInstant());
                booking.setConfirmed(bookingResult.getBoolean("is_confirmed"));

                List<Integer> seatIds = new ArrayList<>();
                try (PreparedStatement seatStmt = connection.prepareStatement(seatQuery)) {
                    seatStmt.setInt(1, booking.getId());
                    ResultSet seatResult = seatStmt.executeQuery();
                    while (seatResult.next()) {
                        seatIds.add(seatResult.getInt("show_seat_id"));
                    }
                }
                booking.setSeatIds(seatIds);

                bookingHistory.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingHistory;
    }


    public User getUserById(int userId) {
        final String query = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("email"), resultSet.getString("phone"),
                        resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUser(int userId, User user) {
        final String query = "UPDATE users SET email = ?, phone = ?, name = ? WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPhone());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setInt(4, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String hashedPassword) {
        final String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
