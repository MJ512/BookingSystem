package org.bookmyshow.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.BookingDAOInterface;
import org.bookmyshow.repository.ShowDAOInterface;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.validation.BookingValidator;

import java.sql.SQLException;
import java.util.logging.Logger;
import java.time.Instant;

@Singleton
public class BookingService {

    private static final Logger logger = Logger.getLogger(BookingService.class.getName());

    private final BookingDAOInterface bookingDAO;
    private final BookingValidator validator;
    private final ShowDAOInterface showDAO;

    @Inject
    private BookingService(final BookingDAOInterface bookingDAO, final ShowDAOInterface showDAO, final BookingValidator validator) {
        this.bookingDAO = bookingDAO;
        this.showDAO = showDAO;
        this.validator = validator;
    }

    public final int bookSeat(final Booking booking) {

        try {
            if (showDAO.hasShowStarted(booking.getShowId())) {
                logger.warning("Booking failed. Show has already started.");
                throw new BookingException("Booking failed. Show has already started.");
            }
        } catch (SQLException e) {
            logger.severe("Database error while checking if show has started: " + e.getMessage());
            throw new BookingException("Database error while checking show start time.", e);
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


    public final boolean cancelBooking(final int bookingId) {
        Booking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null) {
            logger.warning("Cannot cancel. Booking with ID " + bookingId + " does not exist.");
            return false;
        }

        try {
            if (showDAO.hasShowStarted(booking.getShowId())) {
                logger.warning("Cannot cancel. Show with ID " + booking.getShowId() + " has already started.");
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Database error while checking if show has started: " + e.getMessage());
            return false;
        }
        return bookingDAO.cancelBooking(bookingId);
    }
}
