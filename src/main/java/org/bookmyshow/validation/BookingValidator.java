package org.bookmyshow.validation;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bookmyshow.repository.ValidationRepository;

import java.util.List;

/**
 * Validates a booking request by checking user existence,
 * show validity, and seat availability.
 */
@Singleton
public class BookingValidator {

    private final ValidationRepository validationDAO;

    @Inject
    public BookingValidator(final ValidationRepository validationDAO) {
        this.validationDAO = validationDAO;
    }

    public boolean isValidBooking(final int userId,
                                   final int movieShowId,
                                   final List<Integer> seatIds) {
        return validationDAO.isValidUser(userId)
                && validationDAO.isValidMovieShow(movieShowId)
                && validationDAO.areSeatsAvailable(seatIds, movieShowId);
    }
}
