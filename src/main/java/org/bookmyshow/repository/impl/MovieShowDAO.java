package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.repository.ShowDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

@Singleton
public class MovieShowDAO implements ShowDAOInterface {

    private static final Logger logger = Logger.getLogger(MovieShowDAO.class.getName());

    public final boolean hasShowStarted(int showId) {
        final String query = "SELECT start_time FROM movie_show WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, showId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Timestamp startTime = resultSet.getTimestamp("start_time");
                return startTime.toInstant().isBefore(java.time.Instant.now());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public final Integer getShowIdByBookingId(final int bookingId) {
        final String query = "SELECT movie_show_id FROM booking WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("movie_show_id");
            }
        } catch (SQLException e) {
            logger.severe("Database error in getShowIdByBookingId: " + e.getMessage());
        }
        return null;
    }
}
