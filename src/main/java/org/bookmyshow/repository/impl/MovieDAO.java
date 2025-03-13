package org.bookmyshow.repository.impl;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.Movie;
import org.bookmyshow.repository.MovieDAOInterface;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class MovieDAO implements MovieDAOInterface {

    private static final Logger logger = Logger.getLogger(MovieDAO.class.getName());

    @Override
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

                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return movies;
    }

    @Override
    public final List<Movie> getHighestWatchedMovies() {
        final String query = """
        SELECT m.id, m.title, m.certificate, m.language, m.movie_cast, m.genre,
               m.duration, m.release_date, COUNT(b.id) AS total_bookings
        FROM booking b
        JOIN movie_show ms ON b.show_id = ms.id
        JOIN movie m ON ms.movie_id = m.id
        WHERE b.is_confirmed = true
        GROUP BY m.id, m.title, m.certificate, m.language, m.movie_cast, m.genre, m.duration, m.release_date
        ORDER BY total_bookings DESC
        LIMIT 10;
    """;

        final List<Movie> movies = new ArrayList<>();

        try (final Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                movies.add(new Movie(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("certificate"),
                        convertArrayToList(resultSet.getArray("language")),
                        convertArrayToList(resultSet.getArray("genre")),
                        convertArrayToList(resultSet.getArray("movie_cast")),
                        resultSet.getInt("duration"),
                        resultSet.getDate("release_date").toLocalDate()
                ));
            }

        } catch (SQLException e) {
            logger.severe("Database error in getHighestWatchedMovies: " + e.getMessage());
        }

        return movies;
    }

    @Override
    public final List<Movie> fetchMoviesByAddress(final int addressId) {
        String query = """
    SELECT DISTINCT m.id, m.title, m.certificate, m.language, m.genre, 
                    m.duration, m.release_date, 
                    COALESCE(STRING_AGG(mc.cast_name, ','), '') AS movie_cast
    FROM movie m 
    JOIN movie_show ms ON m.id = ms.movie_id 
    JOIN theater t ON ms.theater_id = t.id 
    LEFT JOIN movie_cast mc ON m.id = mc.movie_id 
    WHERE t.address_id = ?
    AND m.is_playing = TRUE
    GROUP BY m.id, m.title, m.certificate, m.language, m.genre, 
             m.duration, m.release_date;
""";

        final List<Movie> movies = new ArrayList<>();

        try (final Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, addressId);
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    movies.add(mapResultSetToMovie(resultSet));
                }
            }

        } catch (SQLException e) {
            logger.severe("Database error in fetchMoviesByAddress: " + e.getMessage());
        }

        return movies;
    }

    private Movie mapResultSetToMovie(final ResultSet resultSet) throws SQLException {
        return new Movie(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("certificate"),
                Arrays.asList(resultSet.getString("language").split(",")),
                Arrays.asList(resultSet.getString("genre").split(",")),
                Arrays.asList(resultSet.getString("movie_cast").split(",")),
                resultSet.getInt("duration"),
                resultSet.getDate("release_date").toLocalDate()
        );
    }
    private List<String> convertArrayToList(final Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return List.of();
        }
        return Arrays.asList((String[]) sqlArray.getArray());
    }

}
