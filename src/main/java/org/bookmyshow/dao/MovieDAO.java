package org.bookmyshow.dao;

import org.bookmyshow.database.PostgreSQLConnection;
import org.bookmyshow.model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDAO {

    public final List<Movie> getPlayingMovies() {
        final List<Movie> movies = new ArrayList<>();
        final String MOVIE_QUERY = "SELECT * FROM movie WHERE is_playing = TRUE";

        try(Connection connection = PostgreSQLConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MOVIE_QUERY);
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

}
