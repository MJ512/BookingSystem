package org.bookingsystemapi.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookingsystemapi.dao.BookingDAO;
import org.bookingsystemapi.dao.ShowDAO;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.validation.BookingValidator;
import java.util.logging.Logger;
import java.time.Instant;

@Singleton
public class BookingService {

    private BookingDAO bookingDAO;
    private BookingValidator validator;
    private ShowDAO showDAO;

    private static final Logger logger = Logger.getLogger(BookingService.class.getName());

    @Inject
    public BookingService(BookingDAO bookingDAO, ShowDAO showDAO, BookingValidator validator) {
        this.bookingDAO = bookingDAO;
        this.showDAO = showDAO;
        this.validator = validator;
    }

    public int bookSeat(Booking booking) {
        // Check if the show has already started
        if (showDAO.hasShowStarted(booking.getShowId())) {
            logger.warning("Booking failed. Show has already started.");
            return -1; // Show already started
        }

        // Validate seat availability and booking rules
        if (!validator.isValidBooking(booking.getUserId(), booking.getTheaterId(), booking.getMovieId(),
                booking.getShowId(), booking.getScreenId(), booking.getSeatIds())) {
            logger.warning("Booking validation failed.");
            return -1;
        }

        // Insert into bookings table
        booking.setBookingTime(Instant.now());
        int bookingId = bookingDAO.createBooking(booking);

        if (bookingId == -1) {
            logger.severe("Booking failed to store in database.");
            return -1;
        }

        // Insert into booking_seats table to map booked seats
        boolean seatsMapped = bookingDAO.mapSeatsToBooking(bookingId, booking.getSeatIds());
        if (!seatsMapped) {
            logger.severe("Failed to map seats for booking ID: " + bookingId);
            return -1;
        }

        logger.info("Booking successful with ID: " + bookingId);
        return bookingId;
    }


    public boolean cancelBooking(int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            logger.warning("Cannot cancel. Booking with ID " + bookingId + " does not exist.");
            return false;
        }

        boolean showStarted = showDAO.hasShowStarted(booking.getShowId());
        if (showStarted) {
            logger.warning("Cannot cancel. Show with ID " + booking.getShowId() + " has already started.");
            return false;
        }

        return bookingDAO.cancelBooking(bookingId);
    }
}
