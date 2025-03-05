package org.bookmyshow.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.dao.UserDAO;
import org.bookmyshow.model.User;
import org.bookmyshow.validation.HashPassword;

import java.sql.SQLException;

@Singleton
public class UserLoginService {

    private final UserDAO userDAO;

    @Inject
    private UserLoginService(UserDAO userDAO) {
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
