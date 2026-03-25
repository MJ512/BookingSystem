package org.bookmyshow.repository.impl;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.repository.AbstractValidationDAO;
import org.bookmyshow.repository.ValidationDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ValidationDAO extends AbstractValidationDAO implements ValidationDAOInterface {

    private static final Logger logger = Logger.getLogger(ValidationDAO.class.getName());

    public final boolean isValidUser(final int userId) {
        return existsInDatabase("users", "id", userId);
    }

    public final boolean isEmailExists(final String email) {
        return existsInDatabase("users", "email", email);
    }

    public final boolean isPhoneNumberExists(final String phone) {
        return existsInDatabase("users", "phone", phone);
    }

    public final boolean isValidMovieShow(final int movieShowId) {
        return existsInDatabase("movie_show", "id", movieShowId);
    }

    public final boolean areSeatsAvailable(final List<Integer> seatIds, final int showId) {
        logger.info("Checking if seats are available for Show ID: " + showId + " | Seats: " + seatIds);

        if (seatIds.isEmpty()) {
            return false;
        }

        String placeholders = String.join(",", seatIds.stream().map(s -> "?").toArray(String[]::new));
        final String query = "SELECT is_booked FROM show_seats WHERE show_id = ? AND seat_id IN (" + placeholders + ")";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, showId);
            for (int i = 0; i < seatIds.size(); i++) {
                preparedStatement.setInt(i + 2, seatIds.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBoolean("is_booked")) {
                    return false;
                }
            }
            return true;

        } catch (SQLException e) {
            logger.severe("Database error in areSeatsAvailable: " + e.getMessage());
            return false;
        }
    }
}
