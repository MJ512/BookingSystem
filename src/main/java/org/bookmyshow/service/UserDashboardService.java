package org.bookmyshow.service;

import jakarta.inject.Inject;
import org.bookmyshow.repository.UserDashboardRepository;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.util.HashPassword;

import java.util.List;

public class UserDashboardService {

    private final UserDashboardRepository userDashboardDAO;

    @Inject
    public UserDashboardService(final UserDashboardRepository userDashboardDAO){
        this.userDashboardDAO = userDashboardDAO;
    }

    public final List<Booking> getBookingHistory(final int userId) {

        return userDashboardDAO.getUserBookingHistory(userId);
    }

    public final boolean updateUserInfo(final int userId, final String password, final User user) {
        User existingUser = userDashboardDAO.getUserById(userId);

        if (existingUser == null) {
            return false;
        }

        if (HashPassword.verifyPassword(password, existingUser.getPassword())) {
            return userDashboardDAO.updateUser(userId, user);
        }
        return false;
    }

    public final boolean changePassword(final int userId, final String oldPassword, final String newPassword) {
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
