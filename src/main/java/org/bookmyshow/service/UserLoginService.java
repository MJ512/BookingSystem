package org.bookmyshow.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.UserRepository;
import org.bookmyshow.model.User;
import org.bookmyshow.util.HashPassword;

import java.sql.SQLException;

@Singleton
public class UserLoginService {

    private final UserRepository userDAO;

    @Inject
    private UserLoginService(final UserRepository userDAO) {
        this.userDAO = userDAO;
    }

    public final User authenticateUser(final String loginInput, final String password) throws SQLException {

        User user = userDAO.getUserByEmailOrPhone(loginInput);

        if (user == null || !HashPassword.verifyPassword(password, user.getPassword())) {
            return null;
        }

        return user;
    }
}
