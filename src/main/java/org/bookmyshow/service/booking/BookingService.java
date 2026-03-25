package org.bookmyshow.service.booking;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.repository.BookingDAOInterface;
import org.bookmyshow.repository.SeatDAOInterface;
import org.bookmyshow.repository.ShowDAOInterface;
import org.bookmyshow.validation.BookingValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;

/**
 * Handles the core business logic for booking and cancellation.
 * Validation and seat mapping are delegated to dedicated components.
 */
@Singleton
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final int INVALID_BOOKING_ID = -1;

    private final BookingDAOInterface bookingDAO;
    private final ShowDAOInterface showDAO;
    private final BookingValidator validator;
    private final SeatDAOInterface seatDAO;

    @Inject
    public BookingService(final BookingDAOInterface bookingDAO,
                          final ShowDAOInterface showDAO,
                          final BookingValidator validator,
                          final SeatDAOInterface seatDAO) {
        this.bookingDAO = bookingDAO;
        this.showDAO = showDAO;
        this.validator = validator;
        this.seatDAO = seatDAO;
    }

    /**
     * Books seats for a given show. Throws {@link BookingException} on any failure.
     *
     * @param booking the booking request
     * @return the new booking ID
     */
    public int bookSeat(final Booking booking) {
        logger.info("Booking request received for Show ID: {}", booking.getMovieShowId());

        checkShowHasNotStarted(booking.getMovieShowId());

        if (!validator.isValidBooking(booking.getUserId(), booking.getMovieShowId(), booking.getSeatIds())) {
            logger.warn("Booking validation failed for user {} / show {}.",
                    booking.getUserId(), booking.getMovieShowId());
            throw new BookingException("Booking validation failed. Check user, show, and seat availability.");
        }

        booking.setBookingTime(Instant.now());

        int bookingId = bookingDAO.createBooking(booking);
        if (bookingId == INVALID_BOOKING_ID) {
            throw new BookingException("Failed to persist booking to the database.");
        }

        if (!seatDAO.mapSeatsToBooking(bookingId, booking.getSeatIds())) {
            throw new BookingException("Failed to map seats to booking ID: " + bookingId);
        }

        logger.info("Booking successful with ID: {}", bookingId);
        return bookingId;
    }

    /**
     * Cancels an existing booking if the show has not yet started.
     *
     * @param userId    the ID of the user requesting cancellation
     * @param bookingId the booking to cancel
     * @return true if cancelled, false if not found or show already started
     */
    public boolean cancelBooking(final int userId, final int bookingId) {
        logger.info("Cancellation request for booking ID: {} by user ID: {}", bookingId, userId);

        try {
            int showId = showDAO.getShowIdByBookingId(bookingId);
            if (showDAO.hasShowStarted(showId)) {
                logger.warn("Cannot cancel booking {}. Show ID {} has already started.", bookingId, showId);
                return false;
            }
        } catch (SQLException e) {
            throw new BookingException("Database error while verifying show start time.", e);
        }

        boolean cancelled = bookingDAO.cancelBooking(userId, bookingId);
        if (cancelled) {
            logger.info("Booking ID {} cancelled successfully for user ID {}.", bookingId, userId);
        } else {
            logger.warn("Cancellation failed for booking ID {} / user ID {}.", bookingId, userId);
        }
        return cancelled;
    }

    private void checkShowHasNotStarted(final int showId) {
        try {
            if (showDAO.hasShowStarted(showId)) {
                throw new BookingException("Booking failed. Show ID " + showId + " has already started.");
            }
        } catch (SQLException e) {
            throw new BookingException("Database error while checking show start time.", e);
        }
    }
}
