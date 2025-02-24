package org.bookingsystemapi.service;

import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;
import org.bookingsystemapi.validation.UserValidation;
import org.bookingsystemapi.validation.Validation;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationService {

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

        try(Connection connection = PostgreSQLConnection.getConnection()){
            connection.setAutoCommit(false);
            String registerQuery = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

            try(PreparedStatement preparedStatement = connection.prepareStatement(registerQuery)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getPhone());
                preparedStatement.setString(4, user.getPassword());

                int rowInserted = preparedStatement.executeUpdate();

                if(rowInserted > 0){
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            } catch (SQLException e) {
                connection.rollback();
                return false;
            }finally {
                connection.setAutoCommit(true);
            }
        }
    }

}
