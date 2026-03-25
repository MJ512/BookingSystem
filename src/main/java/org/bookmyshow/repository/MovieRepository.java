package org.bookmyshow.repository;

import org.bookmyshow.model.Movie;
import org.bookmyshow.model.MovieShow;

import java.util.List;

public interface MovieRepository {

    List<Movie> getPlayingMovies();

    List<Movie> getHighestWatchedMovies();

    List<Movie> fetchMoviesByAddress(final int addressId);

    List<MovieShow> getAvailableMovieShows(final Integer theaterId, final Integer movieId);
}
