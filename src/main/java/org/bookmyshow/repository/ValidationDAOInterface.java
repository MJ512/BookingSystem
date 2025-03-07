package org.bookmyshow.repository;

public interface ValidationDAOInterface {

    boolean isValidUser(final int userId);

    boolean isValidTheater(final int theaterId);

    boolean isValidMovie(final int movieId);

    boolean isValidShow(final int showId);

    boolean isEmailExists(final String email);

    boolean isPhoneNumberExists(final String phone);

    boolean isValidScreen(final int screenId, final int theaterId);

    boolean areSeatsAvailable(final java.util.List<Integer> seatIds, final int showId);
}
