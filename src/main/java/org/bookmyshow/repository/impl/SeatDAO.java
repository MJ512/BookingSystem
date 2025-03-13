package org.bookmyshow.repository.impl;

import org.bookmyshow.database.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SeatDAO {

    private static final Logger logger = Logger.getLogger(SeatDAO.class.getName());

    public final List<Integer> getSeatIdsByBookingId(int bookingId) {
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
