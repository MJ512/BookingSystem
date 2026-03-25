package org.bookmyshow.repository;

public interface ValidationRepository {

    boolean isValidUser(final int userId);

    boolean isEmailExists(final String email);

    boolean isPhoneNumberExists(final String phone);

    boolean areSeatsAvailable(final java.util.List<Integer> seatIds, final int showId);

    boolean isValidMovieShow(final int movieShowId);
}
