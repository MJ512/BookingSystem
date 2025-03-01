package org.bookingsystemapi.service;

import org.bookingsystemapi.dao.UserDAO;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;
import org.bookingsystemapi.validation.UserValidation;
import org.bookingsystemapi.validation.Validation;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class RegistrationService {

    private final UserDAO userDAO;

    public RegistrationService() {
        this.userDAO = new UserDAO();
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
