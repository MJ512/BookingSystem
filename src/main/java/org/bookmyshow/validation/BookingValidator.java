package org.bookmyshow.validation;

import jakarta.inject.Inject;
import org.bookmyshow.repository.ValidationDAOInterface;

import java.util.List;

public class BookingValidator {

    private final ValidationDAOInterface validationDAO;

    @Inject
    private BookingValidator(final ValidationDAOInterface validationDAO){
        this.validationDAO = validationDAO;
    }

    public final boolean isValidBooking(final int userId, final int theaterId, final int movieId,
                                        final int showId, final int screenId, final List<Integer> seatIds){
        return validationDAO.isValidUser(userId) &&
                validationDAO.isValidTheater(theaterId) &&
                validationDAO.isValidMovie(movieId) &&
                validationDAO.isValidShow(showId) &&
               validationDAO.isValidScreen(screenId, theaterId) &&
               validationDAO.areSeatsAvailable(seatIds, showId);
    }
}
