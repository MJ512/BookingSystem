package org.bookingsystemapi.validation;

import jakarta.inject.Inject;
import org.bookingsystemapi.dao.ValidationDAO;

import java.util.List;

public class BookingValidator {

    private final ValidationDAO validationDAO;

    @Inject
    public BookingValidator(ValidationDAO validationDAO){
        this.validationDAO = validationDAO;
    }
    public ValidationDAO getValidationDAO() {
        return validationDAO;
    }

    public boolean isValidBooking(int userId, int theaterId, int movieId, int showId, int screenId, List<Integer> seatIds){
        return validationDAO.isValidUser(userId) &&
                validationDAO.isValidTheater(theaterId) &&
                validationDAO.isValidMovie(movieId) &&
                validationDAO.isValidShow(showId) &&
                validationDAO.isValidScreen(screenId, theaterId) &&
                validationDAO.areSeatsAvailable(seatIds, showId);
    }
}
