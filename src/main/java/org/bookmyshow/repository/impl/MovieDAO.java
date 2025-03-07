package org.bookmyshow.repository.impl;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.Movie;
import org.bookmyshow.repository.MovieDAOInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MovieDAO implements MovieDAOInterface {

    private static final Logger logger = Logger.getLogger(MovieDAO.class.getName());

    public final List<Movie> getPlayingMovies() {
        final List<Movie> movies = new ArrayList<>();
        final String movieQuery = "SELECT * FROM movie WHERE is_playing = TRUE";

        try(final Connection connection = PostgreSQLConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(movieQuery);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while(resultSet.next()){
                Movie movie = new Movie();
                movie.setId(resultSet.getInt("id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setCertificate(resultSet.getString("certificate"));
                movie.setLanguage(Arrays.asList(resultSet.getString("language").split(",")));
                movie.setMovieCast(Arrays.asList(resultSet.getString("movie_cast").split(",")));
                movie.setGenre(Arrays.asList(resultSet.getString("genre").split(",")));
                movie.setDuration(resultSet.getInt("duration"));
                LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
                movie.setReleaseDate(releaseDate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return movies;
    }

    public final Movie getHighestWatchedMovie() {
        final String query = """
                SELECT m.id, m.title, COUNT(b.id) AS total_bookings
                         FROM booking b
                         JOIN movie_show ms ON b.show_id = ms.id
                         JOIN movie m ON ms.movie_id = m.id
                         WHERE b.is_confirmed = true
                         GROUP BY m.id, m.title
                         ORDER BY total_bookings DESC
                         LIMIT 10;
                
                """;

        try (Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return new Movie(
                        resultSet.getInt("id"),
                        resultSet.getString("title")
                );
            }

        } catch (SQLException e) {
            logger.severe("Database error in getHighestWatchedMovie" + e.getMessage());
        }
        return null;
    }

}
