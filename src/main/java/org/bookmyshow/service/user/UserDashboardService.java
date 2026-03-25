package org.bookmyshow.service.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.repository.UserDashboardDAOInterface;
import org.bookmyshow.util.HashPassword;
import org.bookmyshow.validation.PatternValidation;

import java.util.List;

/**
 * Business logic for the user dashboard: booking history, profile updates, password changes.
 */
@Singleton
public class UserDashboardService {

    private final UserDashboardDAOInterface userDashboardDAO;

    @Inject
    public UserDashboardService(final UserDashboardDAOInterface userDashboardDAO) {
        this.userDashboardDAO = userDashboardDAO;
    }

    public List<Booking> getBookingHistory(final int userId) {
        return userDashboardDAO.getUserBookingHistory(userId);
    }

    /**
     * Updates profile info after verifying the current password.
     */
    public boolean updateUserInfo(final int userId, final String password, final User user) {
        User existing = userDashboardDAO.getUserById(userId);
        if (existing == null) return false;
        if (!HashPassword.verifyPassword(password, existing.getPassword())) return false;
        return userDashboardDAO.updateUser(userId, user);
    }

    /**
     * Changes password after validating format and verifying the old password.
     *
     * @throws IllegalArgumentException if the new password does not meet requirements
     */
    public boolean changePassword(final int userId, final String oldPassword, final String newPassword) {
        if (!PatternValidation.isValidPassword(newPassword)) {
            throw new IllegalArgumentException(
                    "New password must be at least 8 characters and include uppercase, lowercase, digit, and special character.");
        }

        User user = userDashboardDAO.getUserById(userId);
        if (user == null) return false;
        if (!HashPassword.verifyPassword(oldPassword, user.getPassword())) return false;

        return userDashboardDAO.updatePassword(userId, HashPassword.hashPassword(newPassword));
    }
}
