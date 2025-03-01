package org.bookingsystemapi.service;

import org.bookingsystemapi.dao.UserDAO;
import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginService {

    private final UserDAO userDAO;

    public UserLoginService() {
        this.userDAO = new UserDAO();
    }

    public User authenticateUser(String loginInput, String password) throws SQLException {

        User user = userDAO.getUserByEmailOrPhone(loginInput);

        if (user == null || !HashPassword.verifyPassword(password, user.getPassword())) {
            throw new SQLException("Invalid credentials");
        }

        // Return User object WITHOUT the password for security
        return new User(user.getUserId(), user.getName(), user.getEmail(), user.getPhone());
    }
}
