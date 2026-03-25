package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.repository.AbstractValidationRepository;
import org.bookmyshow.repository.DatabaseColumn;
import org.bookmyshow.repository.DatabaseTable;
import org.bookmyshow.repository.ValidationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Singleton
public class ValidationRepositoryImpl extends AbstractValidationRepository implements ValidationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ValidationRepositoryImpl.class);

    @Override
    public boolean isValidUser(final int userId) {
        return existsInDatabase(DatabaseTable.USERS, DatabaseColumn.ID, userId);
    }

    @Override
    public boolean isEmailExists(final String email) {
        return existsInDatabase(DatabaseTable.USERS, DatabaseColumn.EMAIL, email);
    }

    @Override
    public boolean isPhoneNumberExists(final String phone) {
        return existsInDatabase(DatabaseTable.USERS, DatabaseColumn.PHONE, phone);
    }

    @Override
    public boolean isValidMovieShow(final int movieShowId) {
        return existsInDatabase(DatabaseTable.MOVIE_SHOW, DatabaseColumn.ID, movieShowId);
    }

    @Override
    public boolean areSeatsAvailable(final List<Integer> seatIds, final int showId) {
        if (seatIds == null || seatIds.isEmpty()) {
            return false;
        }

        String placeholders = "?,".repeat(seatIds.size());
        placeholders = placeholders.substring(0, placeholders.length() - 1);
        final String query = "SELECT is_booked FROM show_seats WHERE show_id = ? AND seat_id IN (" + placeholders + ")";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, showId);
            for (int i = 0; i < seatIds.size(); i++) {
                ps.setInt(i + 2, seatIds.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getBoolean("is_booked")) {
                        logger.info("Seat already booked in show {}.", showId);
                        return false;
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            logger.error("DB error checking seat availability for show {}.", showId, e);
            return false;
        }
    }
}
