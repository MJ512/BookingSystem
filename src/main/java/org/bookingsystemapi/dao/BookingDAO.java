package org.bookingsystemapi.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookingsystemapi.Interface.BookingDAOInterface;
import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.exception.BookingException;
import org.bookingsystemapi.model.Booking;
import java.util.logging.Logger;
import java.sql.*;
import java.util.List;

@Singleton
public class BookingDAO implements BookingDAOInterface {

    private final SeatDAO seatDAO;
    private final ShowDAO showDAO; // Add ShowDAO reference
    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());

    @Inject
    public BookingDAO(SeatDAO seatDAO, ShowDAO showDAO) {
        this.seatDAO = seatDAO;
        this.showDAO = showDAO; // Inject ShowDAO
    }

    @Override
    public int createBooking(Booking booking) {
        String bookingQuery = "INSERT INTO bookings (user_id, theater_id, movie_id, show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery, Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);

            bookingStatement.setInt(1, booking.getUserId());
            bookingStatement.setInt(2, booking.getTheaterId());
            bookingStatement.setInt(3, booking.getMovieId());
            bookingStatement.setInt(4, booking.getShowId());
            bookingStatement.setInt(5, booking.getScreenId());
            bookingStatement.setBoolean(6, booking.isConfirmed());
            bookingStatement.setTimestamp(7, Timestamp.from(booking.getBookingTime()));

            int rowsInserted = bookingStatement.executeUpdate();
            if (rowsInserted == 0) {
                throw new SQLException("Booking failed.");
            }

            try (ResultSet resultSet = bookingStatement.executeQuery()) {
                if (resultSet.next()) {
                    int bookingId = resultSet.getInt(1);
                    connection.commit();
                    logger.info("Booking successful with ID: " + bookingId);
                    return bookingId;
                } else {
                    throw new SQLException("Booking ID not generated.");
                }
            }
        } catch (SQLException e) {
            logger.severe("Database error during booking: " + e.getMessage());
            try {
                PostgreSQLConnection.getConnection().rollback();
                logger.warning("Transaction rolled back.");
            } catch (SQLException rollbackEx) {
                logger.severe("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new BookingException("Failed to create booking: " + e.getMessage());
        }
    }

    public boolean cancelBooking(int bookingId) {
        String deleteSeatsQuery = "DELETE FROM booking_seats WHERE booking_id = ?";
        String deleteBookingQuery = "DELETE FROM bookings WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Check if the booking exists
            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                logger.warning("Cancellation failed. Booking ID " + bookingId + " not found.");
                return false;
            }

            // Check if the show has already started
            if (showDAO.hasShowStarted(booking.getShowId())) {
                logger.warning("Cancellation failed. Show has already started.");
                return false;
            }

            // Delete seat mappings first
            try (PreparedStatement seatStmt = connection.prepareStatement(deleteSeatsQuery)) {
                seatStmt.setInt(1, bookingId);
                seatStmt.executeUpdate();
            }

            // Now delete the booking itself
            try (PreparedStatement bookingStmt = connection.prepareStatement(deleteBookingQuery)) {
                bookingStmt.setInt(1, bookingId);
                int rowsDeleted = bookingStmt.executeUpdate();
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

    public Booking getBookingById(int bookingId) {
        String query = "SELECT * FROM bookings WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
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
                }
            }
        } catch (SQLException e) {
            logger.severe("Database error in getBookingById: " + e.getMessage());
        }
        return null;
    }

    public boolean mapSeatsToBooking(int bookingId, List<Integer> seatIds) {
        String seatInsertQuery = "INSERT INTO booking_seats (booking_id, show_seat_id) VALUES (?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(seatInsertQuery)) {

            connection.setAutoCommit(false);

            for (int seatId : seatIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, seatId);
                statement.addBatch();
            }

            int[] updatedRows = statement.executeBatch();
            connection.commit();

            return updatedRows.length == seatIds.size();

        } catch (SQLException e) {
            logger.severe("Failed to map seats to booking: " + e.getMessage());
            return false;
        }
    }
}
