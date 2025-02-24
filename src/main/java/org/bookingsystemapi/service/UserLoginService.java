package org.bookingsystemapi.service;

import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginService {

    public User authenticateUser(String loginInput, String password) throws SQLException {

        try (Connection connection = PostgreSQLConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("Database Connection Failed");
            }

            String loginQuery = "SELECT id, name, email, phone, password FROM users WHERE email = ? OR phone = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(loginQuery)) {
                preparedStatement.setString(1, loginInput);
                preparedStatement.setString(2, loginInput);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }

                    String storedPassword = resultSet.getString("password");

                    if(!HashPassword.verifyPassword(password, storedPassword)){
                        throw new SQLException("Invalid credentials");
                    }

                    // Return User object with the retrieved data
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            null
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
