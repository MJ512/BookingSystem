package org.bookingsystemapi.servlet;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookingsystemapi.exception.BookingException;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.service.BookingService;
import java.util.logging.Logger;


@Path("/booking")
public class BookingServlet {


    private BookingService bookingService;
    private static final Logger logger = Logger.getLogger(BookingServlet.class.getName());

    @Inject
    public BookingServlet(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookTickets(Booking booking) {
        try {
            // Basic validation
            if (booking.getUserId() <= 0 || booking.getTheaterId() <= 0 || booking.getMovieId() <= 0 ||
                    booking.getShowId() <= 0 || booking.getScreenId() <= 0 || booking.getSeatIds() == null ||
                    booking.getSeatIds().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Invalid booking data\"}").build();
            }

            int bookingId = bookingService.bookSeat(booking);

            if (bookingId > 0) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"message\": \"Ticket booked successfully\", \"bookingId\": " + bookingId + "}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Seat unavailable or validation failed\"}").build();
            }
        } catch (BookingException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Unexpected error during booking: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}").build();
        }
    }

    @DELETE
    @Path("/cancel/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("id") int bookingId) {
        try {
            boolean success = bookingService.cancelBooking(bookingId);

            if (success) {
                return Response.ok("{\"message\": \"Booking canceled successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Cancellation failed. Booking doesn't exist or show has started.\"}")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Unexpected error while canceling booking ID " + bookingId + ": " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error occurred\"}")
                    .build();
        }
    }

}
