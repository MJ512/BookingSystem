package org.bookmyshow.controller;

import org.bookmyshow.model.Movie;
import org.bookmyshow.service.MovieService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PathParam;
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
    public final Response getHighestWatchedMovies() {
        List<Movie> movies = movieService.getHighestWatchedMovie();
        if (!movies.isEmpty()) {
            return Response.ok(movies).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No movies found").build();
    }

    @GET
    @Path("/by-address/{addressId}")
    public final Response getMoviesByAddress(@PathParam("addressId") final int addressId) {
        List<Movie> movies = movieService.fetchMoviesByAddress(addressId);
        if (!movies.isEmpty()) {
            return Response.ok(movies).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No movies found for the given address").build();
    }

}
