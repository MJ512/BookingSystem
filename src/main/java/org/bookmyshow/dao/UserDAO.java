package org.bookmyshow.dao;

import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UserDAO {

    public final boolean saveUser(User user) throws SQLException {
        final String REGISTER_QUERY = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_QUERY)) {

            connection.setAutoCommit(false);

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());

            int rowInserted = preparedStatement.executeUpdate();
            if (rowInserted > 0) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }

        } catch (SQLException e) {
            throw new SQLException("Error saving user: " + e.getMessage(), e);
        }
    }

    public final User getUserByEmailOrPhone(String loginInput) throws SQLException {
        final String QUERY = "SELECT id, name, email, phone, password FROM users WHERE email = ? OR phone = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY)) {

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
