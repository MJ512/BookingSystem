package org.bookmyshow.repository.impl;

import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.model.Movie;
import org.bookmyshow.model.MovieShow;
import org.bookmyshow.repository.MovieRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public class MovieRepositoryImpl implements MovieRepository {

    private static final Logger logger = Logger.getLogger(MovieRepositoryImpl.class.getName());

    @Override
    public final List<Movie> getPlayingMovies() {
        final String query = "SELECT * FROM movie WHERE is_playing = TRUE";
        return fetchEntities(query, this::mapResultSetToMovie);
    }

    @Override
    public final List<Movie> getHighestWatchedMovies() {
        final String query = """
            SELECT m.id, m.title, m.certificate, m.language, m.movie_cast, m.genre,
                   m.duration, m.release_date, COUNT(b.id) AS total_bookings
            FROM booking b
            JOIN movie_show ms ON b.show_id = ms.id
            JOIN movie m ON ms.movie_id = m.id
            WHERE b.is_confirmed = TRUE
            GROUP BY m.id, m.title, m.certificate, m.language, m.movie_cast, m.genre,
                     m.duration, m.release_date
            ORDER BY total_bookings DESC
            LIMIT 10;
        """;
        return fetchEntities(query, this::mapResultSetToMovie);
    }

    @Override
    public final List<Movie> fetchMoviesByAddress(final int addressId) {
        final String query = """
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

        return fetchEntities(query, this::mapResultSetToMovie, addressId);
    }

    @Override
    public final List<MovieShow> getAvailableMovieShows(final Integer theaterId, final Integer movieId) {
        StringBuilder query = new StringBuilder("SELECT * FROM movie_show WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (theaterId != null) {
            query.append(" AND theater_id = ?");
            parameters.add(theaterId);
        }
        if (movieId != null) {
            query.append(" AND movie_id = ?");
            parameters.add(movieId);
        }

        return fetchEntities(query.toString(), this::mapResultSetToShow, parameters.toArray());
    }

    private <T> List<T> fetchEntities(String query, Function<ResultSet, T> mapper, Object... params) {
        List<T> entities = new ArrayList<>();

        try (final Connection connection = PostgreSQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    entities.add(mapper.apply(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.severe("Database error in fetchEntities: " + e.getMessage());
            throw new RuntimeException("Database operation failed", e);
        }

        return entities;
    }


    private Movie mapResultSetToMovie(final ResultSet resultSet) {
        try {
            return new Movie(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("certificate"),
                    convertStringToList(resultSet.getString("language")),
                    convertStringToList(resultSet.getString("genre")),
                    convertStringToList(resultSet.getString("movie_cast")),
                    resultSet.getInt("duration"),
                    resultSet.getDate("release_date").toLocalDate()
            );
        } catch (SQLException e) {
            logger.severe("Error mapping ResultSet to Movie: " + e.getMessage());
            return null;
        }
    }


    private MovieShow mapResultSetToShow(final ResultSet resultSet) {
        try {
            return new MovieShow(
                resultSet.getInt("id"),
                resultSet.getTimestamp("start_time").toLocalDateTime(),
                resultSet.getInt("screen_id"),
                resultSet.getInt("movie_id"),
                resultSet.getInt("theater_id")
        );
        } catch (SQLException e) {
            logger.severe("Error mapping ResultSet to Movie Show: " + e.getMessage());
            return null;
            }
    }

    private List<String> convertStringToList(final String data) {
        return (data == null || data.isBlank()) ? List.of() : Arrays.asList(data.split(","));
    }
}
