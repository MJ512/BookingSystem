package org.bookingsystemapi.dao;

import org.bookingsystemapi.database.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    public boolean updateSeatAvailability(List<Integer> seatIds, boolean isBooked) {
        String query = "UPDATE show_seats SET is_booked = ? WHERE seat_id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            connection.setAutoCommit(false);

            for (int seatId : seatIds) {
                stmt.setBoolean(1, isBooked);
                stmt.setInt(2, seatId);
                stmt.addBatch();
            }

            int[] updatedRows = stmt.executeBatch();
            connection.commit();
            return updatedRows.length == seatIds.size();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Integer> getSeatIdsByBookingId(int bookingId) {
        String query = "SELECT show_seat_id FROM booking_seats WHERE booking_id = ?";
        List<Integer> seatIds = new ArrayList<>();

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                seatIds.add(resultSet.getInt("seat_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seatIds;
    }
}
