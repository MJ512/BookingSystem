package org.bookingsystemapi.validation;

import org.bookingsystemapi.database.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserValidation {

    public static boolean isEmailExists(String email) throws SQLException {

        try (Connection connection = PostgreSQLConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database Connection Failed");
            }

            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // If count > 0, email exists
                }
            }
        }
        return false;
    }

    public static boolean isPhoneNumberExists(String phone) throws SQLException {
        try (Connection connection = PostgreSQLConnection.getConnection()) {

            if (connection == null) {
                throw new SQLException("Database Connection Failed");
            }

            String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, phone);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // If count > 0, phone exists
                }
            }
        }
        return false;
    }

}
