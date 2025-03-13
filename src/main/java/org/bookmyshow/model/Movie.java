package org.bookmyshow.model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie {

    private int id;
    private String title;
    private String certificate;
    private List<String> language;
    private List<String> movieCast;
    private List<String> genre;
    private int duration;
    private LocalDate releaseDate;

    public Movie() {
    }

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

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getCertificate() {
        return certificate;
    }

    public final void setCertificate(final String certificate) {
        this.certificate = certificate;
    }

    public final List<String> getLanguage() {
        return language;
    }

    public final void setLanguage(final List<String> language) {
        this.language = language;
    }

    public final List<String> getMovieCast() {
        return movieCast;
    }

    public void setMovieCast(final List<String> movieCast) {
        this.movieCast = movieCast;
    }

    public final List<String> getGenre() {
        return genre;
    }

    public final void setGenre(final List<String> genre) {
        this.genre = genre;
    }

    public final int getDuration() {
        return duration;
    }

    public final void setDuration(final int duration) {
        this.duration = duration;
    }

    public final LocalDate getReleaseDate() {
        return releaseDate;
    }

    public final void setReleaseDate(final LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
