package org.bookmyshow.repository;

import org.bookmyshow.model.Movie;
import java.sql.SQLException;
import java.util.List;

public interface MovieDAOInterface {

    List<Movie> getPlayingMovies();

    Movie getHighestWatchedMovie();
}
