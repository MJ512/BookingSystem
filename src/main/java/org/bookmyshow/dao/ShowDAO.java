package org.bookmyshow.dao;

import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;

import java.sql.*;

@Singleton
public class ShowDAO {

    public final boolean hasShowStarted(int showId) {
        final String QUERY = "SELECT start_time FROM MovieShow WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {

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
}
