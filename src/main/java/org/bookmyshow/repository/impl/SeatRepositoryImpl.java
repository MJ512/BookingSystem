package org.bookmyshow.repository.impl;

import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.repository.SeatRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SeatRepositoryImpl implements SeatRepository {

    private static final Logger logger = Logger.getLogger(SeatRepositoryImpl.class.getName());

    @Override
    public boolean mapSeatsToBooking(int bookingId, List<Integer> seatIds) {
        String sql = "INSERT INTO booked_seats (booking_id, seat_id) VALUES (?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);

            for (int seatId : seatIds) {
                statement.setInt(1, bookingId);
                statement.setInt(2, seatId);
                statement.addBatch();
            }

            int[] updatedRows = statement.executeBatch();

            if (updatedRows.length != seatIds.size()) {
                connection.rollback();
                throw new BookingException("Not all seats could be booked. Rolling back.");
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new BookingException("One or more selected seats are already booked.", e);
            }
            throw new BookingException("Database error during booking.", e);
        }
    }

    public final List<Integer> getSeatIdsByBookingId(final int bookingId) {
        final String query = "SELECT seat_id FROM booked_seats WHERE booking_id = ?";
        final List<Integer> seatIds = new ArrayList<>();

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            logger.info("Executing query: " + query + " with bookingId: " + bookingId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    seatIds.add(resultSet.getInt("seat_id"));
                }
            }
        } catch (SQLException e) {
            logger.severe("Database error in getSeatIdsByBookingId: " + e.getMessage());
        }
        return seatIds;
    }
}
