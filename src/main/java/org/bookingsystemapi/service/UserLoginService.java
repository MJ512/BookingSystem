package org.bookingsystemapi.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookingsystemapi.dao.UserDAO;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;

import java.sql.SQLException;

@Singleton
public class UserLoginService {

    private final UserDAO userDAO;

    @Inject
    public UserLoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticateUser(String loginInput, String password) throws SQLException {

        User user = userDAO.getUserByEmailOrPhone(loginInput);

        if (user == null || !HashPassword.verifyPassword(password, user.getPassword())) {
            throw new SQLException("Invalid credentials");
        }

        return new User(user.getUserId(), user.getName(), user.getEmail(), user.getPhone());
    }
}
