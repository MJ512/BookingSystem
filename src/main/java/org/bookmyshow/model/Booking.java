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

    public Booking(final int id, final int userId, final int theaterId,
                   final int movieId, final int showId, final int screenId,
                   final List<Integer> seatIds, final Instant bookingTime, final boolean isConfirmed) {
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

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final int getUserId() {
        return userId;
    }


    public final int getTheaterId() {
        return theaterId;
    }

    public final int getMovieId() {
        return movieId;
    }

    public final int getShowId() {
        return showId;
    }

    public final int getScreenId() {
        return screenId;
    }

    public final List<Integer> getSeatIds() {
        return seatIds;
    }

    public final Instant getBookingTime() {
        return bookingTime;
    }

    public final void setBookingTime(Instant bookingTime) {
        this.bookingTime = bookingTime;
    }

    public final boolean isConfirmed() {
        return isConfirmed;
    }

}
