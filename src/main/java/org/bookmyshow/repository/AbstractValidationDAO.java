package org.bookmyshow.repository;

import org.bookmyshow.database.PostgreSQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractValidationDAO {

    private static final Logger logger = Logger.getLogger(AbstractValidationDAO.class.getName());

    protected final boolean existsInDatabase(final String tableName, final String columnName, final Object value) {

        List<String> validTables = List.of("movie_show", "users");
        List<String> validColumns = List.of("id", "email", "phone");

        if (!validTables.contains(tableName) || !validColumns.contains(columnName)) {
            throw new IllegalArgumentException("Invalid table or column name.");
        }

        final String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + columnName + " = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (value instanceof Integer) {
                preparedStatement.setInt(1, (Integer) value);
            } else if (value instanceof String) {
                preparedStatement.setString(1, (String) value);
            } else {
                throw new IllegalArgumentException("Unsupported data type for existsInDatabase method.");
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;

        } catch (SQLException e) {
            logger.severe("Database error in existsInDatabase: " + e.getMessage());
            return false;
        }
    }
}
