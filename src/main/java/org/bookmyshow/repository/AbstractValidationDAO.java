package org.bookmyshow.repository;

import org.bookmyshow.database.PostgreSQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class AbstractValidationDAO {

    private static final Logger logger = Logger.getLogger(AbstractValidationDAO.class.getName());

    protected final boolean existsInDatabase(final String tableName, final String columnName, final Object value) {
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

    protected final boolean updateUserDetails(final int userId, final Map<String, String> columnValueMap) {
        if (columnValueMap.isEmpty()) return false;

        final StringBuilder queryBuilder = new StringBuilder("UPDATE users SET ");
        columnValueMap.forEach((column, value) -> queryBuilder.append(column).append(" = ?, "));
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE id = ?");

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            int index = 1;
            for (String value : columnValueMap.values()) {
                preparedStatement.setString(index++, value);
            }
            preparedStatement.setInt(index, userId);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.severe("Database error in updateUserDetails: " + e.getMessage());
            return false;
        }
    }

    protected final boolean updateUserPassword(final int userId, final String hashedPassword) {
        final String query = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, hashedPassword);
            preparedStatement.setInt(2, userId);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.severe("Database error in updateUserPassword: " + e.getMessage());
            return false;
        }
    }

    protected final <T> T fetchRecordById(final String tableName, final String column, int id, Function<ResultSet, T> mapper) {
        final String query = "SELECT * FROM " + tableName + " WHERE " + column + " = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? mapper.apply(resultSet) : null;

        } catch (SQLException e) {
            logger.severe("Database error while fetching record from " + tableName + ": " + e.getMessage());
            throw new RuntimeException("Error fetching record from " + tableName, e);
        }
    }

}
