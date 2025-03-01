package org.bookingsystemapi.dao;

import org.bookingsystemapi.database.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SeatDAO {

    private static final Logger logger = Logger.getLogger(SeatDAO.class.getName());

    public List<Integer> getSeatIdsByBookingId(int bookingId) {
        String query = "SELECT show_seat_id FROM booking_seats WHERE booking_id = ?";
        List<Integer> seatIds = new ArrayList<>();

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                seatIds.add(resultSet.getInt("show_seat_id")); // Fixed column name
            }
        } catch (SQLException e) {
            logger.severe("Database error in getSeatIdsByBookingId: " + e.getMessage());
        }
        return seatIds;
    }
}
