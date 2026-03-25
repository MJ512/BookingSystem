package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SeatRepositoryImpl implements SeatRepository {

    private static final Logger logger = LoggerFactory.getLogger(SeatRepositoryImpl.class);

    private static final String INSERT_SEAT = "INSERT INTO booked_seats (booking_id, seat_id) VALUES (?, ?)";
    private static final String SELECT_SEATS = "SELECT seat_id FROM booked_seats WHERE booking_id = ?";
    private static final String DUPLICATE_KEY = "23505";

    @Override
    public boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds) {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SEAT)) {

            conn.setAutoCommit(false);
            for (int seatId : seatIds) {
                ps.setInt(1, bookingId);
                ps.setInt(2, seatId);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            if (results.length != seatIds.size()) {
                conn.rollback();
                throw new BookingException("Not all seats could be mapped. Rolled back.");
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (DUPLICATE_KEY.equals(e.getSQLState())) {
                throw new BookingException("One or more selected seats are already booked.", e);
            }
            throw new BookingException("Database error mapping seats to booking.", e);
        }
    }

    @Override
    public List<Integer> getSeatIdsByBookingId(final int bookingId) {
        final List<Integer> seatIds = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_SEATS)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seatIds.add(rs.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error fetching seats for booking {}.", bookingId, e);
        }
        return seatIds;
    }
}
