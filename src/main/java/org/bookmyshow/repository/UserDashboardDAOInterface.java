package org.bookmyshow.repository;

import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;

import java.util.List;

public interface UserDashboardDAOInterface {

    List<Booking> getUserBookingHistory(final int userId);

    User getUserById(final int userId);

    boolean updateUser(final int userId, final User user);

    boolean updatePassword(final int userId, final String hashedPassword);

}
