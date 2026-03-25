package org.bookmyshow.model;

import java.time.LocalDateTime;

public class MovieShow {

    private final LocalDateTime startTime;
    private final int screenId;
    private final int movieId;
    private final int theaterId;

    private int id;

    public MovieShow(final int id, final LocalDateTime startTime, final int screenId, final int movieId, final int theaterId) {
        this.id = id;
        this.startTime = startTime;
        this.screenId = screenId;
        this.movieId = movieId;
        this.theaterId = theaterId;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getScreenId() {
        return screenId;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getTheaterId() {
        return theaterId;
    }
}
