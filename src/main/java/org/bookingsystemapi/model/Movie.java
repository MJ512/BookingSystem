package org.bookingsystemapi.model;


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

    public Movie(LocalDate releaseDate, int duration,
                 List<String> genre, List<String> movieCast,
                 List<String> language, String certificate, String title, int id) {

        this.releaseDate = releaseDate;
        this.duration = duration;
        this.language = new ArrayList<>();
        this.movieCast = new ArrayList<>();
        this.genre = new ArrayList<>();
        this.certificate = certificate;
        this.title = title;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<String> getMovieCast() {
        return movieCast;
    }

    public void setMovieCast(List<String> movieCast) {
        this.movieCast = movieCast;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}
