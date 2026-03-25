package org.bookmyshow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movie ticket booking.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    private int id;
    private int userId;
    private int movieShowId;
    private Instant bookingTime;
    private boolean isConfirmed;
    private List<Integer> seatIds;

    /** No-arg constructor required by Jackson. */
    public Booking() {
        this.seatIds = new ArrayList<>();
    }

    public Booking(final int userId, final int movieShowId,
                   final List<Integer> seatIds, final boolean isConfirmed) {
        this.userId = userId;
        this.movieShowId = movieShowId;
        this.bookingTime = Instant.now();
        this.isConfirmed = isConfirmed;
        this.seatIds = seatIds != null ? new ArrayList<>(seatIds) : new ArrayList<>();
    }

    public int getId()                { return id; }
    public int getUserId()            { return userId; }
    public int getMovieShowId()       { return movieShowId; }
    public Instant getBookingTime()   { return bookingTime; }
    public boolean isConfirmed()      { return isConfirmed; }
    public List<Integer> getSeatIds() { return seatIds; }

    public void setId(int id)                      { this.id = id; }
    public void setUserId(int userId)              { this.userId = userId; }
    public void setMovieShowId(int movieShowId)    { this.movieShowId = movieShowId; }
    public void setBookingTime(Instant bookingTime){ this.bookingTime = bookingTime; }
    public void setConfirmed(boolean confirmed)    { this.isConfirmed = confirmed; }
    public void setSeatIds(List<Integer> seatIds)  { this.seatIds = seatIds; }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", userId=" + userId + ", movieShowId=" + movieShowId
                + ", bookingTime=" + bookingTime + ", confirmed=" + isConfirmed
                + ", seats=" + seatIds + '}';
    }
}
