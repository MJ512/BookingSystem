package org.bookingsystemapi.servlet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookingsystemapi.model.Booking;
import org.bookingsystemapi.service.BookingService;


@Path("/booking")
public class BookingServlet {

    private final BookingService bookingService;

    public BookingServlet(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bookTickets(Booking booking) {
        try {
            int bookingId = bookingService.bookSeat(booking);

            if (bookingId > 0) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"message\": \"Ticket booked successfully\", \"bookingId\": " + bookingId + "}").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Booking validation failed or seat unavailable\"}").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}").build();
        }
    }

    @DELETE
    @Path("/{bookingId}/{showId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelBooking(@PathParam("bookingId") int bookingId, @PathParam("showId") int showId) {
        boolean success = bookingService.cancelBooking(bookingId, showId);

        if (success) {
            return Response.ok("{\"message\": \"Booking canceled successfully\"}").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Cancellation failed. Either the show has started or booking doesn't exist.\"}")
                    .build();
        }
    }
}
