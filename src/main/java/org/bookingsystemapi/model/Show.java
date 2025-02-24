package org.bookingsystemapi.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class Show {

    private int id;
    private LocalDateTime startTime;
    private Movie movie;
    private Theater theater;

    public Show(int id, LocalDateTime startTime,LocalDateTime endTime, Movie movie, Theater theater) {
        this.id = id;
        this.startTime = startTime;
        this.movie = movie;
        this.theater = theater;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }
}
