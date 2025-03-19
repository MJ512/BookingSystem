package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.repository.BookingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

@Singleton
public class BookingRepositoryImpl implements BookingRepository {

    private static final Logger logger = Logger.getLogger(BookingRepositoryImpl.class.getName());

    @Override
    public final int createBooking(final Booking booking) {
        final String insertQuery = "INSERT INTO booking (user_id, movie_show_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            connection.setAutoCommit(false);

            insertStatement.setInt(1, booking.getUserId());
            insertStatement.setInt(2, booking.getMovieShowId());
            insertStatement.setBoolean(3, booking.isConfirmed());
            insertStatement.setTimestamp(4, Timestamp.from(booking.getBookingTime()));

            try (ResultSet resultSet = insertStatement.executeQuery()) {
                if (resultSet.next()) {
                    int bookingId = resultSet.getInt("id");
                    connection.commit();
                    logger.info("Booking successful with ID: " + bookingId);
                    return bookingId;
                }
            }
            connection.rollback();
            logger.severe("Booking failed, rolling back transaction.");
            return -1;
        } catch (SQLException e) {
            logger.severe("Database error during booking: " + e.getMessage());
            throw new BookingException("Database error during booking.", e);
        }
    }

    @Override
    public final boolean cancelBooking(final int userId, final int bookingId) {
        String query = "DELETE FROM booking WHERE id = ? AND user_id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            statement.setInt(1, bookingId);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                connection.rollback();
                logger.warning("Cancellation failed. Either booking not found or user not authorized.");
                return false;
            }

            connection.commit();
            logger.info("Booking ID " + bookingId + " successfully canceled by User ID " + userId);
            return true;

        } catch (SQLException e) {
            logger.severe("Error canceling booking: " + e.getMessage());
        }
        return false;
    }
}
