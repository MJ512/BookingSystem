package org.bookingsystemapi.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookingsystemapi.dao.BookingDAO;
import org.bookingsystemapi.dao.ShowDAO;
import org.bookingsystemapi.exception.BookingException;
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

        if (showDAO.hasShowStarted(booking.getShowId())) {
            logger.warning("Booking failed. Show has already started.");
            throw new BookingException("Booking failed. Show has already started.");
        }

        if (!validator.isValidBooking(booking.getUserId(), booking.getTheaterId(), booking.getMovieId(),
                booking.getShowId(), booking.getScreenId(), booking.getSeatIds())) {
            logger.warning("Booking validation failed.");
            throw new BookingException("Booking validation failed.");
        }

        // Insert into bookings table
        booking.setBookingTime(Instant.now());
        int bookingId = bookingDAO.createBooking(booking);

        if (bookingId == -1) {
            logger.severe("Booking failed to store in database.");
            throw new BookingException("Failed to store booking.");
        }

        boolean seatsMapped = bookingDAO.mapSeatsToBooking(bookingId, booking.getSeatIds());
        if (!seatsMapped) {
            logger.severe("Failed to map seats for booking ID: " + bookingId);
            throw new BookingException("Failed to map seats.");
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

        if (showDAO.hasShowStarted(booking.getShowId())) {
            logger.warning("Cannot cancel. Show with ID " + booking.getShowId() + " has already started.");
            return false;
        }

        return bookingDAO.cancelBooking(bookingId);
    }
}
