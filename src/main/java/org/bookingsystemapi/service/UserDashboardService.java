package org.bookingsystemapi.service;

import org.bookingsystemapi.dao.UserDashboardDAO;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;

import java.util.List;

public class UserDashboardService {
    private final UserDashboardDAO userDAO = new UserDashboardDAO();

    public List<User> getBookingHistory(int userId) {
        return userDAO.getUserBookingHistory(userId);
    }

    public boolean updateUserInfo(int userId, String password, User user) {
        User existingUser = userDAO.getUserById(userId);
        if (existingUser != null && HashPassword.verifyPassword(password, existingUser.getPassword())) {
            return userDAO.updateUser(userId, user);
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.getUserById(userId);
        if (user != null && HashPassword.verifyPassword(oldPassword, user.getPassword())) {
            String hashedPassword = HashPassword.hashPassword(newPassword);
            return userDAO.updatePassword(userId, hashedPassword);
        }
        return false;
    }

    public boolean sendPasswordResetLink(String emailOrPhone) {
        User user = userDAO.getUserByEmailOrPhone(emailOrPhone);
        if (user != null) {
            String resetToken = generateResetToken();
            userDAO.storePasswordResetToken(user.getUserId(), resetToken);
            sendResetEmail(user.getEmail(), resetToken);
            return true;
        }
        return false;
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private void sendResetEmail(String email, String resetToken) {
        System.out.println("Sending password reset link to: " + email + " with token: " + resetToken);
    }
}
