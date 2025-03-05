package org.bookmyshow.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.dao.UserDAO;
import org.bookmyshow.model.User;
import org.bookmyshow.validation.HashPassword;
import org.bookmyshow.validation.UserValidation;
import org.bookmyshow.validation.Validation;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Singleton
public class RegistrationService {

    private final UserDAO userDAO;

    @Inject
    private RegistrationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean registerUser(User user) throws SQLException, NoSuchAlgorithmException{

        if (UserValidation.isEmailExists(user.getEmail()) || UserValidation.isPhoneNumberExists(user.getPhone())){
            throw new SQLException("Email or Phone Number already exists.");
        }

        if (!Validation.isValidEmail(user.getEmail())){
            throw new IllegalArgumentException("Enter valid Email");
        }

        if (!Validation.isValidNumber(user.getPhone())){
            throw new IllegalArgumentException("Invalid phone number. It must be exactly 10 digits.");
        }

        if (!Validation.isValidPassword(user.getPassword())){
            throw new IllegalArgumentException("Invalid password. It must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }

        String hashedPassword = HashPassword.hashPassword(user.getPassword());

        user.setPassword(hashedPassword);

        return userDAO.saveUser(user);
    }

}
