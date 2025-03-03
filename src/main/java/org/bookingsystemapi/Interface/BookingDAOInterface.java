package org.bookingsystemapi.Interface;

import org.bookingsystemapi.model.Booking;

public interface BookingDAOInterface {
    int createBooking(Booking booking);
    boolean cancelBooking(int bookingId);
}
