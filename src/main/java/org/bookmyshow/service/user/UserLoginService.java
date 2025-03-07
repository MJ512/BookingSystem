package org.bookmyshow.service.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.UserDAOInterface;
import org.bookmyshow.model.User;
import org.bookmyshow.util.HashPassword;

import java.sql.SQLException;

@Singleton
public class UserLoginService {

    private final UserDAOInterface userDAO;

    @Inject
    private UserLoginService(final UserDAOInterface  userDAO) {
        this.userDAO = userDAO;
    }

    public final User authenticateUser(final String loginInput, final String password) throws SQLException {

        User user = userDAO.getUserByEmailOrPhone(loginInput);

        if (user == null || !HashPassword.verifyPassword(password, user.getPassword())) {
            throw new SQLException("Invalid credentials");
        }

        return new User(user.getUserId(), user.getName(), user.getEmail(), user.getPhone());
    }
}
