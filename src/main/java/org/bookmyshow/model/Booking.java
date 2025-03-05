package org.bookmyshow.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {

    private int id;
    private int userId;
    private int theaterId;
    private int movieId;
    private int showId;
    private int screenId;
    private List<Integer> seatIds;
    private Instant bookingTime;
    private boolean isConfirmed;

    public Booking() {
    }

    public Booking(int id, int userId, int theaterId, int movieId, int showId, int screenId, List<Integer> seatIds, Instant bookingTime, boolean isConfirmed) {
        this.id = id;
        this.userId = userId;
        this.theaterId = theaterId;
        this.movieId = movieId;
        this.showId = showId;
        this.screenId = screenId;
        this.seatIds = (seatIds != null) ? Collections.unmodifiableList(new ArrayList<>(seatIds))
                : Collections.emptyList();

        this.bookingTime = (bookingTime != null) ? bookingTime : Instant.now();
        this.isConfirmed = isConfirmed;
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

    public int getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(int theaterId) {
        this.theaterId = theaterId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
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

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
