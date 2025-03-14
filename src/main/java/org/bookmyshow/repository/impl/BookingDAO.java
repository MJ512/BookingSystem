package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.repository.AbstractValidationDAO;
import org.bookmyshow.repository.BookingDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

@Singleton
public class BookingDAO extends AbstractValidationDAO implements BookingDAOInterface {

    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());

    @Override
    public final int createBooking(final Booking booking) {
        final String insertQuery = "INSERT INTO booking (user_id, theater_id, movie_id, movie_show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            connection.setAutoCommit(false);

            insertStatement.setInt(1, booking.getUserId());
            insertStatement.setInt(2, booking.getTheaterId());
            insertStatement.setInt(3, booking.getMovieId());
            insertStatement.setInt(4, booking.getShowId());
            insertStatement.setInt(5, booking.getScreenId());
            insertStatement.setBoolean(6, booking.isConfirmed());
            insertStatement.setTimestamp(7, Timestamp.from(booking.getBookingTime()));

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
    public boolean cancelBooking(int bookingId) {
        String query = "DELETE FROM booking WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            statement.setInt(1, bookingId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                connection.rollback();
                logger.warning("Cancellation failed. Booking not found.");
                return false;
            }

            connection.commit();
            logger.info("Booking ID " + bookingId + " successfully canceled.");
            return true;

        } catch (SQLException e) {
            logger.severe("Error canceling booking: " + e.getMessage());
        }
        return false;
    }
}
