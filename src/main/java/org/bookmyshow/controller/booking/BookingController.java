package org.bookmyshow.controller.booking;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookmyshow.exception.BookingException;
import org.bookmyshow.model.Booking;
import org.bookmyshow.service.booking.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for booking and cancellation operations.
 */
@Path("/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @Inject
    public BookingController(final BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * POST /booking
     * Books one or more seats for a movie show.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookTickets(final Booking booking) {
        if (booking == null || booking.getUserId() <= 0 || booking.getMovieShowId() <= 0
                || booking.getSeatIds() == null || booking.getSeatIds().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"userId, movieShowId and at least one seatId are required\"}")
                    .build();
        }

        try {
            int bookingId = bookingService.bookSeat(booking);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Ticket booked successfully\", \"bookingId\": " + bookingId + "}")
                    .build();
        } catch (BookingException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error during booking.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred. Please try again later.\"}")
                    .build();
        }
    }

    /**
     * DELETE /booking/cancel/{user_id}/{id}
     * Cancels an existing booking, provided the show has not started.
     */
    @DELETE
    @Path("/cancel/{user_id}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("user_id") final int userId,
                                  @PathParam("id") final int bookingId) {
        try {
            boolean success = bookingService.cancelBooking(userId, bookingId);
            if (success) {
                return Response.ok("{\"message\": \"Booking cancelled successfully\"}").build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Booking not found, already cancelled, or show has already started.\"}")
                    .build();
        } catch (BookingException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error while cancelling booking ID {}.", bookingId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred. Please try again later.\"}")
                    .build();
        }
    }
}
