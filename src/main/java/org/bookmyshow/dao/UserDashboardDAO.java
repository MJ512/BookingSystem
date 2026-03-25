package org.bookmyshow.dao;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardDAO {

    public final List<Booking> getUserBookingHistory(int userId) {
        final List<Booking> bookingHistory = new ArrayList<>();
        final String BOOKING_QUERY = "SELECT * FROM booking WHERE user_id = ?";
        final String SEAT_QUERY = "SELECT show_seat_id FROM booking_seats WHERE booking_id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement bookingStmt = connection.prepareStatement(BOOKING_QUERY)) {
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
                try (PreparedStatement seatStatement = connection.prepareStatement(SEAT_QUERY)) {
                    seatStatement.setInt(1, booking.getId());
                    ResultSet seatResult = seatStatement.executeQuery();
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
        final String QUERY = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {
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
        final String QUERY = "UPDATE users SET email = ?, phone = ?, name = ? WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {
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
        final String QUERY = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {
            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
