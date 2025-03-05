package org.bookmyshow.service;

import jakarta.inject.Inject;
import org.bookmyshow.dao.UserDashboardDAO;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.validation.HashPassword;

import java.util.List;

public class UserDashboardService {
    private final UserDashboardDAO userDashboardDAO;

    @Inject
    public UserDashboardService(UserDashboardDAO userDashboardDAO){
        this.userDashboardDAO = userDashboardDAO;
    }

    public List<Booking> getBookingHistory(int userId) {

        return userDashboardDAO.getUserBookingHistory(userId);
    }

    public boolean updateUserInfo(int userId, String password, User user) {
        User existingUser = userDashboardDAO.getUserById(userId);

        if (existingUser == null) {
            return false;
        }

        if (HashPassword.verifyPassword(password, existingUser.getPassword())) {
            return userDashboardDAO.updateUser(userId, user);
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDashboardDAO.getUserById(userId);

        if (user == null) {
            return false;
        }

        if (!HashPassword.verifyPassword(oldPassword, user.getPassword())) {
            return false;
        }

        String hashedPassword = HashPassword.hashPassword(newPassword);

        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }

        return userDashboardDAO.updatePassword(userId, hashedPassword);
    }
}
