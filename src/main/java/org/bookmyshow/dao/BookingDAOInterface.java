package org.bookmyshow.dao;

import org.bookmyshow.model.Booking;

public interface BookingDAOInterface {

    int createBooking(Booking booking);

    boolean cancelBooking(int bookingId);
}
