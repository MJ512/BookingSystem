package org.bookingsystemapi.dao;

import org.bookingsystemapi.Interface.BookingDAOInterface;
import org.bookingsystemapi.database.PostgreSQLConnection;
import org.bookingsystemapi.model.Booking;

import java.sql.*;

import java.util.List;

public class BookingDAO implements BookingDAOInterface {
    private final SeatDAO seatDAO;

    public BookingDAO(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    @Override
    public int createBooking(Booking booking) {
        String bookingQuery = "INSERT INTO bookings (user_id, theater_id, movie_id, show_id, screen_id, is_confirmed, booking_time) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery, Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);

            bookingStatement.setInt(1, booking.getUserId());
            bookingStatement.setInt(2, booking.getTheaterId());
            bookingStatement.setInt(3, booking.getMovieId());
            bookingStatement.setInt(4, booking.getShowId());
            bookingStatement.setInt(5, booking.getScreenId());
            bookingStatement.setBoolean(6, booking.isConfirmed());
            bookingStatement.setTimestamp(7, Timestamp.from(booking.getBookingTime()));

            int rowsInserted = bookingStatement.executeUpdate();
            if (rowsInserted == 0) throw new SQLException("Booking failed.");

            try (ResultSet generatedKeys = bookingStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    connection.commit();
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Booking ID not generated.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public boolean cancelBooking(int bookingId) {
        String cancelQuery = "DELETE FROM bookings WHERE id = ?";

        try(Connection connection = PostgreSQLConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(cancelQuery)) {

            connection.setAutoCommit(false);

            List<Integer> seatIds = seatDAO.getSeatIdsByBookingId(bookingId);

            if(seatIds.isEmpty()){
                connection.rollback();
                return false;
            }

            preparedStatement.setInt(1, bookingId);
            int cancelBooking = preparedStatement.executeUpdate();

            if(cancelBooking == 0){
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
           e.printStackTrace();
           return false;
        }
    }
}
