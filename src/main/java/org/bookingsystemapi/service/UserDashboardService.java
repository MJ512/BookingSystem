package org.bookingsystemapi.service;

import org.bookingsystemapi.dao.UserDashboardDAO;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.validation.HashPassword;

import java.util.List;

public class UserDashboardService {
    private final UserDashboardDAO userDAO = new UserDashboardDAO();

    public List<Booking> getBookingHistory(int userId) {
        return userDAO.getUserBookingHistory(userId);
    }

    public boolean updateUserInfo(int userId, String password, User user) {
        User existingUser = userDAO.getUserById(userId);

        // Ensure user exists before checking password
        if (existingUser == null) {
            return false;
        }

        // Verify password before allowing the update
        if (HashPassword.verifyPassword(password, existingUser.getPassword())) {
            return userDAO.updateUser(userId, user);
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.getUserById(userId);

        // Ensure user exists before checking password
        if (user == null) {
            return false;
        }

        // Verify the old password before allowing the update
        if (!HashPassword.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        // Hash the new password
        String hashedPassword = HashPassword.hashPassword(newPassword);

        // Ensure hashing didn't fail before updating
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }

        return userDAO.updatePassword(userId, hashedPassword);
    }
}
