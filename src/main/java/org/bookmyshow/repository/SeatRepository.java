package org.bookmyshow.repository;

import java.util.List;

public interface SeatRepository {

    boolean mapSeatsToBooking(final int bookingId, final List<Integer> seatIds);

    List<Integer> getSeatIdsByBookingId(final int bookingId);
}
