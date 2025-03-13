package org.bookmyshow.repository;

import org.bookmyshow.model.Booking;

import java.util.List;

public interface BookingDAOInterface {

    int createBooking(final Booking booking);

    boolean cancelBooking(final int bookingId);

    boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds);

    Integer getShowIdByBookingId(final int bookingId);
}
