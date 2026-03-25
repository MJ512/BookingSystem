package org.bookmyshow.service.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDAOInterface;
import org.bookmyshow.util.HashPassword;

import java.sql.SQLException;

/**
 * Authenticates users by verifying their credentials against stored BCrypt hashes.
 */
@Singleton
public class UserLoginService {

    private final UserDAOInterface userDAO;

    @Inject
    public UserLoginService(final UserDAOInterface userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * @param loginInput email or phone number
     * @param password   raw (unhashed) password
     * @return the authenticated {@link User}, or {@code null} if credentials are invalid
     */
    public User authenticateUser(final String loginInput, final String password) throws SQLException {
        User user = userDAO.getUserByEmailOrPhone(loginInput);
        if (user == null || !HashPassword.verifyPassword(password, user.getPassword())) {
            return null;
        }
        return user;
    }
}
