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

    public final boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds) {
        final String insertQuery = "INSERT INTO booked_seats (booking_id, seat_id) VALUES (?, ?)";

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
