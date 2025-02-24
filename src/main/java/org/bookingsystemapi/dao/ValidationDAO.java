package org.bookingsystemapi.dao;

import org.bookingsystemapi.database.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ValidationDAO {

    public boolean isValidUser(int userId) { return existsInDatabase("SELECT COUNT(*) FROM users WHERE id = ?", userId); }
    public boolean isValidTheater(int theaterId) { return existsInDatabase("SELECT COUNT(*) FROM theaters WHERE id = ?", theaterId); }
    public boolean isValidMovie(int movieId) { return existsInDatabase("SELECT COUNT(*) FROM movies WHERE id = ?", movieId); }
    public boolean isValidShow(int showId) { return existsInDatabase("SELECT COUNT(*) FROM shows WHERE id = ?", showId); }

    public boolean isValidScreen(int screenId, int theaterId) {
        String query = "SELECT COUNT(*) FROM screens WHERE id = ? AND theater_id = ?";
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, screenId);
            preparedStatement.setInt(2, theaterId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean areSeatsAvailable(List<Integer> seatIds, int showId) {
        System.out.println("Checking if seats are available for Show ID: " + showId + " | Seats: " + seatIds);

        String placeholders = String.join(",", seatIds.stream().map(s -> "?").toArray(String[]::new));
        String query = "SELECT seat_id, is_booked FROM show_seats WHERE show_id = ? AND seat_id IN (" + placeholders + ")";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, showId);
            for (int i = 0; i < seatIds.size(); i++) {
                preparedStatement.setInt(i + 2, seatIds.get(i)); // Set seat IDs
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            boolean available = true;

            while (resultSet.next()) {
                int seatId = resultSet.getInt("seat_id");
                boolean isBooked = resultSet.getBoolean("is_booked");
                System.out.println("Seat " + seatId + " | isBooked: " + isBooked);

                if (isBooked) {
                    available = false;
                }
            }

            System.out.println("Seats available: " + available);
            return available;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existsInDatabase(String query, int id) {
        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

