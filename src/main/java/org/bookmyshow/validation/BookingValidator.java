package org.bookmyshow.validation;

import jakarta.inject.Inject;
import org.bookmyshow.repository.ValidationRepository;

import java.util.List;

public class BookingValidator {

    private final ValidationRepository validationDAO;

    @Inject
    private BookingValidator(final ValidationRepository validationDAO){
        this.validationDAO = validationDAO;
    }

    public final boolean isValidBooking(final int userId, final int movieShowId, final List<Integer> seatIds){
        return validationDAO.isValidUser(userId) &&
                validationDAO.isValidMovieShow(movieShowId) &&
                validationDAO.areSeatsAvailable(seatIds, movieShowId);
    }
}
