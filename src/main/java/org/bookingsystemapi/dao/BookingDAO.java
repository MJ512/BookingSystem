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
    private static final Logger logger = Logger.getLogger(BookingDAO.class.getName());

    @Inject
    public BookingDAO(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    @Override
    public int createBooking(Booking booking) {
        final String insertBookingQuery = "INSERT INTO bookings (user_id, theater_id, movie_id, show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        final String selectBookingIdQuery = "SELECT id FROM bookings WHERE user_id = ? AND theater_id = ? AND movie_id = ? AND show_id = ? AND screen_id = ? ORDER BY booking_time DESC LIMIT 1";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertBookingQuery);
             PreparedStatement selectStatement = connection.prepareStatement(selectBookingIdQuery)) {

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

    public boolean cancelBooking(int bookingId) {
        final String deleteSeatsQuery = "DELETE FROM booking_seats WHERE booking_id = ?";
        final String deleteBookingQuery = "DELETE FROM bookings WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            connection.setAutoCommit(false);

            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                logger.warning("Cancellation failed. Booking ID " + bookingId + " not found.");
                return false;
            }

            try (PreparedStatement seatStmt = connection.prepareStatement(deleteSeatsQuery)) {
                seatStmt.setInt(1, bookingId);
                seatStmt.executeUpdate();
            }

            try (PreparedStatement bookingStatement = connection.prepareStatement(deleteBookingQuery)) {
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

    public Booking getBookingById(int bookingId) {
        final String query = "SELECT * FROM bookings WHERE id = ?";

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
        final String seatInsertQuery = "INSERT INTO booking_seats (booking_id, show_seat_id) VALUES (?, ?)";

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
