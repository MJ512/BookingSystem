package org.bookmyshow.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.bookmyshow.model.Movie;
import org.bookmyshow.repository.MovieDAOInterface;

import java.util.List;

@ApplicationScoped
public class MovieService {

    private final MovieDAOInterface movieDAO;

    public MovieService(final MovieDAOInterface movieDAO) {
        this.movieDAO = movieDAO;
    }

    public List<Movie> getPlayingMovies() {
        return movieDAO.getPlayingMovies();
    }

    public Movie getHighestWatchedMovie() {
        return movieDAO.getHighestWatchedMovie();
    }
}
