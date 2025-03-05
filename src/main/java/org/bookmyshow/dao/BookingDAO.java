package org.bookmyshow.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import java.util.logging.Logger;
import java.sql.*;
import java.util.List;

@Singleton
public class BookingDAO implements BookingDAOInterface {

    private final SeatDAO seatDAO;
    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());

    @Inject
    private BookingDAO(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    @Override
    public final int createBooking(final Booking booking) {
        final String INSERT_BOOKING_QUERY = "INSERT INTO booking (user_id, theater_id, movie_id, show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        final String SELECT_BOOKING_ID_QUERY = "SELECT id FROM booking WHERE user_id = ? AND theater_id = ? AND movie_id = ? AND show_id = ? AND screen_id = ? ORDER BY booking_time DESC LIMIT 1";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_BOOKING_QUERY);
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_BOOKING_ID_QUERY)) {

            connection.setAutoCommit(false);

            insertStatement.setInt(1, booking.getUserId());
            insertStatement.setInt(2, booking.getTheaterId());
            insertStatement.setInt(3, booking.getMovieId());
            insertStatement.setInt(4, booking.getShowId());
            insertStatement.setInt(5, booking.getScreenId());
            insertStatement.setBoolean(6, booking.isConfirmed());
            insertStatement.setTimestamp(7, Timestamp.from(booking.getBookingTime()));

            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {

                selectStatement.setInt(1, booking.getUserId());
                selectStatement.setInt(2, booking.getTheaterId());
                selectStatement.setInt(3, booking.getMovieId());
                selectStatement.setInt(4, booking.getShowId());
                selectStatement.setInt(5, booking.getScreenId());

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int bookingId = resultSet.getInt("booking_id");
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
            throw new BookingException("Database error during booking." , e);
        }
    }

    public final boolean cancelBooking(final int bookingId) {
        final String DELETE_SEAT_QUERY = "DELETE FROM booking_seats WHERE booking_id = ?";
        final String DELETE_BOOKING_QUERY = "DELETE FROM booking WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            connection.setAutoCommit(false);

            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                logger.warning("Cancellation failed. Booking ID " + bookingId + " not found.");
                return false;
            }

            try (PreparedStatement seatStmt = connection.prepareStatement(DELETE_SEAT_QUERY)) {
                seatStmt.setInt(1, bookingId);
                seatStmt.executeUpdate();
            }

            try (PreparedStatement bookingStatement = connection.prepareStatement(DELETE_BOOKING_QUERY)) {
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

    public final Booking getBookingById(final int bookingId) {
        final String query = "SELECT * FROM booking WHERE id = ?";

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

    public final boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds) {
        final String SEAT_INSERT_QUERY = "INSERT INTO booking_seats (booking_id, show_seat_id) VALUES (?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEAT_INSERT_QUERY)) {

            connection.setAutoCommit(false);

            for (int seatId : seatIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, seatId);
                statement.addBatch();
            }

            int[] updatedRows = statement.executeBatch();

            if (updatedRows.length != seatIds.size()) {
                connection.rollback(); // Rollback on failure
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
