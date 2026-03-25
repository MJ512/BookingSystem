package org.bookmyshow.service.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDAOInterface;
import org.bookmyshow.repository.ValidationDAOInterface;
import org.bookmyshow.util.HashPassword;
import org.bookmyshow.validation.PatternValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Handles new user registration: validates input, checks uniqueness,
 * hashes the password, then persists the user.
 */
@Singleton
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final UserDAOInterface userDAO;
    private final ValidationDAOInterface validationDAO;

    @Inject
    public RegistrationService(final UserDAOInterface userDAO,
                                final ValidationDAOInterface validationDAO) {
        this.userDAO = userDAO;
        this.validationDAO = validationDAO;
    }

    /**
     * Registers a new user.
     *
     * @throws IllegalArgumentException if input format is invalid
     * @throws SQLException             if email/phone already exists or a DB error occurs
     */
    public boolean registerUser(final User user) throws SQLException {
        if (!PatternValidation.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email address format.");
        }
        if (!PatternValidation.isValidNumber(user.getPhone())) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
        }
        if (!PatternValidation.isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters and include uppercase, lowercase, digit, and special character.");
        }
        if (validationDAO.isEmailExists(user.getEmail()) || validationDAO.isPhoneNumberExists(user.getPhone())) {
            throw new SQLException("Email or phone number already registered.");
        }

        user.setPassword(HashPassword.hashPassword(user.getPassword()));

        boolean saved = userDAO.saveUser(user);
        if (saved) {
            logger.info("User registered: {}", user.getEmail());
        } else {
            logger.warn("Registration did not persist for: {}", user.getEmail());
        }
        return saved;
    }
}
