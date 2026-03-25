package org.bookmyshow.model;

import java.time.LocalDateTime;


public class Show {

    private int id;
    private LocalDateTime startTime;
    private Movie movie;
    private Theater theater;

    public Show(final int id, final LocalDateTime startTime,final LocalDateTime endTime, final Movie movie, final Theater theater) {
        this.id = id;
        this.startTime = startTime;
        this.movie = movie;
        this.theater = theater;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public final LocalDateTime getStartTime() {
        return startTime;
    }

    public final Movie getMovie() {
        return movie;
    }

    public final Theater getTheater() {
        return theater;
    }

}
