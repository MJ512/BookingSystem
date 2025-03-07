package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UserDAO implements UserDAOInterface {

    @Override
    public final boolean saveUser(final User user) throws SQLException {
        final String registerQuery = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(registerQuery)) {

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getPassword());

            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public User getUserByEmailOrPhone(final String loginInput) throws SQLException {
        final String query = "SELECT id, name, email, phone, password FROM users WHERE email = ? OR phone = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, loginInput);
            preparedStatement.setString(2, loginInput);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("password")
                );
            }
        }
    }
}
