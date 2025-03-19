package org.bookmyshow.repository;

import org.bookmyshow.model.Booking;

public interface BookingRepository {

    int createBooking(final Booking booking);

    boolean cancelBooking(final int userId, final int bookingId);
}
