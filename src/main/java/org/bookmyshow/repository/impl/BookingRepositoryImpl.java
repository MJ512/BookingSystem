package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Singleton
public class BookingRepositoryImpl implements BookingRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookingRepositoryImpl.class);

    private static final String INSERT_BOOKING =
            "INSERT INTO booking (user_id, movie_show_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String DELETE_BOOKING =
            "DELETE FROM booking WHERE id = ? AND user_id = ?";

    @Override
    public int createBooking(final Booking booking) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_BOOKING)) {

            conn.setAutoCommit(false);
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getMovieShowId());
            ps.setBoolean(3, booking.isConfirmed());
            ps.setTimestamp(4, Timestamp.from(booking.getBookingTime()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int bookingId = rs.getInt("id");
                    conn.commit();
                    logger.info("Booking created with ID: {}", bookingId);
                    return bookingId;
                }
            }
            conn.rollback();
            logger.error("Booking insert returned no ID — rolled back.");
            return -1;

        } catch (SQLException e) {
            logger.error("Database error creating booking.", e);
            throw new BookingException("Database error creating booking.", e);
        }
    }

    @Override
    public boolean cancelBooking(final int userId, final int bookingId) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BOOKING)) {

            conn.setAutoCommit(false);
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                logger.warn("Cancel failed — booking {} not found for user {}.", bookingId, userId);
                return false;
            }
            conn.commit();
            logger.info("Booking {} cancelled for user {}.", bookingId, userId);
            return true;

        } catch (SQLException e) {
            logger.error("Database error cancelling booking {}.", bookingId, e);
            throw new BookingException("Database error cancelling booking.", e);
        }
    }
}
