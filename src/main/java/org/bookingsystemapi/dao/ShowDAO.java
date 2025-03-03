package org.bookingsystemapi.dao;

import jakarta.inject.Singleton;
import org.bookingsystemapi.database.PostgreSQLConnection;

import java.sql.*;

@Singleton
public class ShowDAO {

    public boolean hasShowStarted(int showId) {
        final String query = "SELECT start_time FROM shows WHERE id = ?";

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
}
