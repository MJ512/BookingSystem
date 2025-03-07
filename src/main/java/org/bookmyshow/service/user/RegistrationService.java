package org.bookmyshow.service.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.UserDAOInterface;
import org.bookmyshow.repository.ValidationDAOInterface;
import org.bookmyshow.model.User;
import org.bookmyshow.util.HashPassword;
import org.bookmyshow.validation.Validation;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Logger;

@Singleton
public class RegistrationService {

    private static final Logger logger = Logger.getLogger(RegistrationService.class.getName());
    private final UserDAOInterface userDAO;
    private final ValidationDAOInterface validationDAO;

    @Inject
    private RegistrationService(final UserDAOInterface userDAO, final ValidationDAOInterface validationDAO) {
        this.userDAO = userDAO;
        this.validationDAO = validationDAO;
    }

    public final boolean registerUser(final User user) throws SQLException, NoSuchAlgorithmException{
        if (validationDAO.isEmailExists(user.getEmail()) || validationDAO.isPhoneNumberExists(user.getPhone())) {
            throw new SQLException("Email or Phone Number already exists.");
        }

        if (!Validation.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Enter a valid Email");
        }

        if (!Validation.isValidNumber(user.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number. It must be exactly 10 digits.");
        }

        if (!Validation.isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Invalid password. It must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }

        String hashedPassword = HashPassword.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        final boolean saved = userDAO.saveUser(user);
        if (saved) {
            logger.info("User registration successful: " + user.getEmail());
        } else {
            logger.warning("User registration failed: " + user.getEmail());
        }
        return saved;
    }
}

