package org.bookmyshow.service;

import jakarta.inject.Singleton;
import org.bookmyshow.model.Movie;
import org.bookmyshow.model.MovieShow;
import org.bookmyshow.repository.MovieRepository;

import java.util.List;

@Singleton
public class MovieService {

    private final MovieRepository movieDAO;

    public MovieService(final MovieRepository movieDAO) {
        this.movieDAO = movieDAO;
    }

    public List<Movie> getPlayingMovies() {
        return movieDAO.getPlayingMovies();
    }

    public List<Movie> getHighestWatchedMovie() {
        return movieDAO.getHighestWatchedMovies();
    }

    public List<Movie> fetchMoviesByAddress(final int addressId){
        return movieDAO.fetchMoviesByAddress(addressId);
    }

    public List<MovieShow> getAvailableMovieShows(final int theaterId, final int movieId) {
        return movieDAO.getAvailableMovieShows(theaterId, movieId);
    }

}
