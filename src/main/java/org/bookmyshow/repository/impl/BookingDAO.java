package org.bookmyshow.repository.impl;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.repository.AbstractValidationDAO;
import org.bookmyshow.repository.BookingDAOInterface;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class BookingDAO extends AbstractValidationDAO implements BookingDAOInterface {

    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());
    private final SeatDAO seatDAO;

    @Inject
    private BookingDAO(final SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    @Override
    public final int createBooking(final Booking booking) {
        final String interQuery = "INSERT INTO booking (user_id, theater_id, movie_id, show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        final String selectQuery = "SELECT id FROM booking WHERE user_id = ? AND theater_id = ? AND movie_id = ? AND show_id = ? AND screen_id = ? ORDER BY booking_time DESC LIMIT 1";

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertStatement = connection.prepareStatement(interQuery)) {
                insertStatement.setInt(1, booking.getUserId());
                insertStatement.setInt(2, booking.getTheaterId());
                insertStatement.setInt(3, booking.getMovieId());
                insertStatement.setInt(4, booking.getShowId());
                insertStatement.setInt(5, booking.getScreenId());
                insertStatement.setBoolean(6, booking.isConfirmed());
                insertStatement.setTimestamp(7, Timestamp.from(booking.getBookingTime()));
                insertStatement.executeUpdate();
            }

            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, booking.getUserId());
                selectStatement.setInt(2, booking.getTheaterId());
                selectStatement.setInt(3, booking.getMovieId());
                selectStatement.setInt(4, booking.getShowId());
                selectStatement.setInt(5, booking.getScreenId());

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int bookingId = resultSet.getInt("id");
                        connection.commit();
                        logger.info("Booking successful with ID: " + bookingId);
                        return bookingId;
                    }
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
    public final boolean cancelBooking(final int bookingId) {
        final String deleteQuery = "DELETE FROM booking WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            connection.setAutoCommit(false);

            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                logger.warning("Cancellation failed. Booking ID " + bookingId + " not found.");
                return false;
            }

            try (PreparedStatement bookingStatement = connection.prepareStatement(deleteQuery)) {
                bookingStatement.setInt(1, bookingId);
                int rowsDeleted = bookingStatement.executeUpdate();
                if (rowsDeleted == 0) {
                    connection.rollback();
                    logger.warning("Cancellation failed. Booking not found.");
                    return false;
                }
            }
            connection.commit();
            logger.info("Booking ID " + bookingId + " successfully canceled.");
            return true;

        } catch (SQLException e) {
            logger.severe("SQL Exception while canceling booking ID " + bookingId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public final Booking getBookingById(final int bookingId) {
        return fetchRecordById("booking", "id", bookingId, resultSet -> {
            try {
                List<Integer> seatIds = seatDAO.getSeatIdsByBookingId(bookingId);
                return new Booking(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("theater_id"),
                        resultSet.getInt("movie_id"),
                        resultSet.getInt("show_id"),
                        resultSet.getInt("screen_id"),
                        seatIds,
                        resultSet.getTimestamp("booking_time").toInstant(),
                        resultSet.getBoolean("is_confirmed")
                );
            } catch (SQLException e) {
                throw new BookingException("Error retrieving booking data", e);
            }
        });
    }

    public final boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds) {
        final String insertQuery = "INSERT INTO booked_seats (booking_id, show_seat_id) VALUES (?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            connection.setAutoCommit(false);

            for (int seatId : seatIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, seatId);
                statement.addBatch();
            }

            int[] updatedRows = statement.executeBatch();

            if (updatedRows.length != seatIds.size()) {
                connection.rollback();
                throw new BookingException("Failed to insert all seats for booking ID: " + bookingId);
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            logger.severe("Failed to map seats to booking: " + e.getMessage());
            throw new BookingException("Database error while mapping seats.", e);
        }
    }
}
