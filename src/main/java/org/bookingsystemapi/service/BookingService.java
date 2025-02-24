package org.bookingsystemapi.service;

import org.bookingsystemapi.dao.BookingDAO;
import org.bookingsystemapi.dao.ShowDAO;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.validation.BookingValidator;

import java.time.Instant;

public class BookingService {
    private final BookingDAO bookingDAO;
    private final BookingValidator validator;
    private final ShowDAO showDAO;

    public BookingService(BookingDAO bookingDAO, ShowDAO showDAO, BookingValidator validator) {
        this.bookingDAO = bookingDAO;
        this.showDAO = showDAO;
        this.validator = validator;
    }

    public int bookSeat(Booking booking) {
        // 🔹 Validate before processing
        if (!validator.isValidBooking(booking.getUserId(), booking.getTheaterId(), booking.getMovieId(),
                booking.getShowId(), booking.getScreenId(), booking.getSeatIds())) {
            System.err.println("Booking validation failed!");
            return -1;
        }

        booking.setBookingTime(Instant.now()); // Set the booking timestamp
        int bookingId = bookingDAO.createBooking(booking);

        if (bookingId == -1) {
            System.err.println("Booking failed to store in DB!");
        } else {
            System.out.println("Booking successful with ID: " + bookingId);
        }

        return bookingId;
    }

    public boolean cancelBooking(int bookingId, int showId) {

        if (showDAO.hasShowStarted(showId)) {
            System.err.println("Cannot cancel. The show has already started.");
            return false;
        }

        // Proceed with cancellation
        return bookingDAO.cancelBooking(bookingId);
    }
}
