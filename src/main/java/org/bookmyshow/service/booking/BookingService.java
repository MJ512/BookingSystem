package org.bookmyshow.service.booking;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.BookingDAOInterface;
import org.bookmyshow.repository.SeatDAOInterface;
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
    private static final int INVALID_BOOKING_ID = -1;

    private final BookingDAOInterface bookingDAO;
    private final ShowDAOInterface showDAO;
    private final BookingValidator validator;
    private final SeatDAOInterface seatDAO;

    @Inject
    public BookingService(final BookingDAOInterface bookingDAO, final ShowDAOInterface showDAO,
                          final BookingValidator validator, final SeatDAOInterface seatDAO) {
        this.bookingDAO = bookingDAO;
        this.showDAO = showDAO;
        this.validator = validator;
        this.seatDAO = seatDAO;
    }

    public final int bookSeat(final Booking booking) {
        logger.info("Booking request received for Show ID: " + booking.getMovieShowId());

        try {
            if (showDAO.hasShowStarted(booking.getMovieShowId())) {
                logger.warning("Booking failed. Show has already started.");
                throw new BookingException("Booking failed. Show has already started.");
            }
        } catch (SQLException e) {
            logger.severe("Database error while checking if show has started: " + e.getMessage());
            throw new BookingException("Database error while checking show start time.", e);
        }

        if (!validator.isValidBooking(booking.getUserId(), booking.getMovieShowId(), booking.getSeatIds())) {
            logger.warning("Booking validation failed.");
            throw new BookingException("Booking validation failed.");
        }

        booking.setBookingTime(Instant.now());

        int bookingId = bookingDAO.createBooking(booking);
        if (bookingId == INVALID_BOOKING_ID) {
            logger.severe("Booking failed to store in database.");
            throw new BookingException("Failed to store booking.");
        }

        if (!seatDAO.mapSeatsToBooking(bookingId, booking.getSeatIds())) {
            logger.severe("Failed to map seats for booking ID: " + bookingId);
            throw new BookingException("Failed to map seats.");
        }

        logger.info("Booking successful with ID: " + bookingId);
        return bookingId;
    }

    public final boolean cancelBooking(final int userId, final int bookingId) {
        logger.info("Cancellation request received for Booking ID: " + bookingId + " by User ID: " + userId);

        try {
            int showId = showDAO.getShowIdByBookingId(bookingId);

            if (showDAO.hasShowStarted(showId)) {
                logger.warning("Cannot cancel. Show with ID " + showId + " has already started.");
                return false;
            }
        } catch (SQLException e) {
            logger.severe("Database error while checking if show has started: " + e.getMessage());
            throw new BookingException("Database error while checking show start time.", e);
        }

        boolean isCancelled = bookingDAO.cancelBooking(userId, bookingId);
        if (isCancelled) {
            logger.info("Booking ID " + bookingId + " canceled successfully by User ID " + userId);
        } else {
            logger.warning("Failed to cancel Booking ID: " + bookingId + " for User ID: " + userId);
        }
        return isCancelled;
    }
}
