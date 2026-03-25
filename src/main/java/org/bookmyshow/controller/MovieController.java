package org.bookmyshow.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.PathParam;
import org.bookmyshow.model.Movie;
import org.bookmyshow.model.MovieShow;
import org.bookmyshow.service.MovieService;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.logging.Logger;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {

    private static final Logger logger = Logger.getLogger(MovieController.class.getName());

    private final MovieService movieService;

    @Inject
    private MovieController(final MovieService movieService) {
        this.movieService = movieService;
    }

    @GET
    @Path("/playing")
    public final Response getPlayingMovies() {
        List<Movie> movies = movieService.getPlayingMovies();
        return Response.ok(movies).build();
    }

    @GET
    @Path("/top-watched")
    public final Response getHighestWatchedMovies() {
        List<Movie> movies = movieService.getHighestWatchedMovie();
        if (!movies.isEmpty()) {
            return Response.ok(movies).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No movies found").build();
    }

    @GET
    @Path("/by-address/{address_id}")
    public final Response getMoviesByAddress(@PathParam("address_id") final int addressId) {
        List<Movie> movies = movieService.fetchMoviesByAddress(addressId);
        if (!movies.isEmpty()) {
            return Response.ok(movies).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No movies found for the given address").build();
    }

    @GET
    @Path("/available-movie-shows")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableMovieShows(@QueryParam("theater_id") final Integer theaterId,
                                           @QueryParam("movie_id") final Integer movieId) {
        try {
            List<MovieShow> movieShows = movieService.getAvailableMovieShows(theaterId, movieId);
            return Response.ok(movieShows).build();
        } catch (Exception e) {
            logger.severe("Error fetching available movie shows: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Could not fetch movie shows\"}").build();
        }
    }
}
