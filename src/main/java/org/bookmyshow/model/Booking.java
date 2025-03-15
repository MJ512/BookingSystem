package org.bookmyshow.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {

    private final boolean isConfirmed;
    private final List<Integer> seatIds;

    private int id;
    private int userId;
    private int movieShowId;
    private Instant bookingTime;


    public Booking(int userId, int movieShowId, List<Integer> seatIds, boolean isConfirmed) {
        this.userId = userId;
        this.movieShowId = movieShowId;
        this.bookingTime = Instant.now();
        this.isConfirmed = isConfirmed;
        this.seatIds = (seatIds != null) ? Collections.unmodifiableList(new ArrayList<>(seatIds))
                : Collections.emptyList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieShowId() {
        return movieShowId;
    }

    public void setMovieShowId(int movieShowId) {
        this.movieShowId = movieShowId;
    }

    public Instant getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Instant bookingTime) {
        this.bookingTime = bookingTime;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", movieShowId=" + movieShowId +
                ", bookingTime=" + bookingTime +
                ", isConfirmed=" + isConfirmed +
                ", seatIds=" + seatIds +
                '}';
    }
}
