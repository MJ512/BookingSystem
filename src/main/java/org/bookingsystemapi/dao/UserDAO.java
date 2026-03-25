package org.bookingsystemapi.dao;

import jakarta.inject.Singleton;
import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UserDAO {

    public boolean saveUser(User user) throws SQLException {
        final String registerQuery = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(registerQuery)) {

            connection.setAutoCommit(false); // Start transaction

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());

            int rowInserted = preparedStatement.executeUpdate();
            if (rowInserted > 0) {
                connection.commit(); // Commit if success
                return true;
            } else {
                connection.rollback(); // Rollback if failure
                return false;
            }

        } catch (SQLException e) {
            throw new SQLException("Error saving user: " + e.getMessage(), e);
        }
    }

    public User getUserByEmailOrPhone(String loginInput) throws SQLException {
        final String query = "SELECT id, name, email, phone, password FROM users WHERE email = ? OR phone = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, loginInput);
            preparedStatement.setString(2, loginInput);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null; // User not found
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("password") // Storing hashed password
                );
            }

        } catch (SQLException e) {
            throw new SQLException("Error fetching user: " + e.getMessage(), e);
        }
    }
}
