package org.bookmyshow.repository.impl;

import jakarta.inject.Singleton;
import org.bookmyshow.datasource.PostgreSQLConnection;
import org.bookmyshow.model.Movie;
import org.bookmyshow.model.MovieShow;
import org.bookmyshow.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Singleton
public class MovieRepositoryImpl implements MovieRepository {

    private static final Logger logger = LoggerFactory.getLogger(MovieRepositoryImpl.class);

    private static final String SELECT_PLAYING =
            "SELECT * FROM movie WHERE is_playing = TRUE";

    private static final String SELECT_HIGHEST_WATCHED = """
            SELECT m.id, m.title, m.certificate, m.language, m.movie_cast, m.genre,
                   m.duration, m.release_date, COUNT(b.id) AS total_bookings
            FROM booking b
            JOIN movie_show ms ON b.show_id = ms.id
            JOIN movie m ON ms.movie_id = m.id
            WHERE b.is_confirmed = TRUE
            GROUP BY m.id, m.title, m.certificate, m.language, m.movie_cast,
                     m.genre, m.duration, m.release_date
            ORDER BY total_bookings DESC
            LIMIT 10
            """;

    private static final String SELECT_BY_ADDRESS = """
            SELECT DISTINCT m.id, m.title, m.certificate, m.language, m.genre,
                            m.duration, m.release_date,
                            COALESCE(STRING_AGG(mc.cast_name, ','), '') AS movie_cast
            FROM movie m
            JOIN movie_show ms ON m.id = ms.movie_id
            JOIN theater t ON ms.theater_id = t.id
            LEFT JOIN movie_cast mc ON m.id = mc.movie_id
            WHERE t.address_id = ? AND m.is_playing = TRUE
            GROUP BY m.id, m.title, m.certificate, m.language, m.genre, m.duration, m.release_date
            """;

    @Override
    public List<Movie> getPlayingMovies() {
        return fetchEntities(SELECT_PLAYING, this::mapMovie);
    }

    @Override
    public List<Movie> getHighestWatchedMovies() {
        return fetchEntities(SELECT_HIGHEST_WATCHED, this::mapMovie);
    }

    @Override
    public List<Movie> fetchMoviesByAddress(final int addressId) {
        return fetchEntities(SELECT_BY_ADDRESS, this::mapMovie, addressId);
    }

    @Override
    public List<MovieShow> getAvailableMovieShows(final Integer theaterId, final Integer movieId) {
        StringBuilder query = new StringBuilder("SELECT * FROM movie_show WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (theaterId != null) { query.append(" AND theater_id = ?"); params.add(theaterId); }
        if (movieId != null)   { query.append(" AND movie_id = ?");   params.add(movieId); }
        return fetchEntities(query.toString(), this::mapMovieShow, params.toArray());
    }

    // ── Generic helper ─────────────────────────────────────────────────────────

    private <T> List<T> fetchEntities(final String query,
                                       final Function<ResultSet, T> mapper,
                                       final Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    T entity = mapper.apply(rs);
                    if (entity != null) results.add(entity);
                }
            }
        } catch (SQLException e) {
            logger.error("DB error in fetchEntities: {}", query, e);
            throw new RuntimeException("Database operation failed.", e);
        }
        return results;
    }

    // ── Mappers ────────────────────────────────────────────────────────────────

    private Movie mapMovie(final ResultSet rs) {
        try {
            return new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("certificate"),
                    toList(rs.getString("language")),
                    toList(rs.getString("genre")),
                    toList(rs.getString("movie_cast")),
                    rs.getInt("duration"),
                    rs.getDate("release_date").toLocalDate()
            );
        } catch (SQLException e) {
            logger.error("Error mapping ResultSet to Movie.", e);
            return null;
        }
    }

    private MovieShow mapMovieShow(final ResultSet rs) {
        try {
            return new MovieShow(
                    rs.getInt("id"),
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getInt("screen_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("theater_id")
            );
        } catch (SQLException e) {
            logger.error("Error mapping ResultSet to MovieShow.", e);
            return null;
        }
    }

    private List<String> toList(final String csv) {
        return (csv == null || csv.isBlank()) ? List.of() : Arrays.asList(csv.split(","));
    }
}
