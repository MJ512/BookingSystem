package org.bookmyshow.repository;

import java.sql.SQLException;

public interface ShowRepository {

    boolean hasShowStarted(final int showId) throws SQLException;

    Integer getShowIdByBookingId(final int bookingId);
}
