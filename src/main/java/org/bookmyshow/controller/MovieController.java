package org.bookmyshow.controller;

import org.bookmyshow.model.Movie;
import org.bookmyshow.service.MovieService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {

    @Inject
    private MovieService movieService;

    @GET
    @Path("/playing")
    public final Response getPlayingMovies() {
        List<Movie> movies = movieService.getPlayingMovies();
        return Response.ok(movies).build();
    }

    @GET
    @Path("/top-watched")
    public final Response getHighestWatchedMovie() {
        Movie movie = movieService.getHighestWatchedMovie();
        if (movie != null) {
            return Response.ok(movie).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No movie found").build();
    }
}
