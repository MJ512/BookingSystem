package org.bookmyshow.validation;

import jakarta.inject.Inject;
import org.bookmyshow.dao.ValidationDAO;

import java.util.List;

public class BookingValidator {

    private final ValidationDAO validationDAO;

    @Inject
    private BookingValidator(ValidationDAO validationDAO){
        this.validationDAO = validationDAO;
    }

    public final boolean isValidBooking(int userId, int theaterId, int movieId, int showId, int screenId, List<Integer> seatIds){
        return validationDAO.isValidUser(userId) &&
                validationDAO.isValidTheater(theaterId) &&
                validationDAO.isValidMovie(movieId) &&
                validationDAO.isValidShow(showId) &&
                validationDAO.isValidScreen(screenId, theaterId) &&
                validationDAO.areSeatsAvailable(seatIds, showId);
    }
}
