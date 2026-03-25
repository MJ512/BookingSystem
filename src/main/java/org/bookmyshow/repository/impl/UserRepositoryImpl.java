package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private static final String INSERT_USER =
            "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
    private static final String SELECT_USER_BY_LOGIN =
            "SELECT id, name, email, phone, password FROM users WHERE email = ? OR phone = ?";

    @Override
    public boolean saveUser(final User user) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public User getUserByEmailOrPhone(final String loginInput) throws SQLException {
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_USER_BY_LOGIN)) {

            ps.setString(1, loginInput);
            ps.setString(2, loginInput);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password")
                );
            }
        }
    }
}
