package org.bookmyshow.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie {

    private final String title;
    private final String certificate;
    private final List<String> language;
    private final List<String> movieCast;
    private final List<String> genre;
    private final int duration;
    private final LocalDate releaseDate;

    private int id;

    public Movie(final int id, final String title, final String certificate,
                 final List<String> language, final List<String> genre, final List<String> movieCast,
                 final int duration, final LocalDate releaseDate) {

        this.id = id;
        this.title = title;
        this.certificate = certificate;
        this.genre = genre != null ? List.copyOf(genre) : List.of();
        this.movieCast = movieCast != null ? List.copyOf(movieCast) : List.of();
        this.language = language != null ? List.copyOf(language) : List.of();
        this.duration = duration;
        this.releaseDate = releaseDate;
    }

    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final String getTitle() {
        return title;
    }

    public final String getCertificate() {
        return certificate;
    }

    public final List<String> getLanguage() {
        return language;
    }

    public final List<String> getMovieCast() {
        return movieCast;
    }

    public final List<String> getGenre() {
        return genre;
    }

    public final int getDuration() {
        return duration;
    }

    public final LocalDate getReleaseDate() {
        return releaseDate;
    }

}
