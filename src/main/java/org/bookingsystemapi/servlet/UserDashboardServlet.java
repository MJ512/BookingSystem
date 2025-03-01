package org.bookingsystemapi.servlet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.service.UserDashboardService;
import org.bookingsystemapi.model.Booking;

import java.util.List;

@Path("/users")
public class UserDashboardServlet {
    private final UserDashboardService userService = new UserDashboardService();

    @GET
    @Path("/history/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingHistory(@PathParam("userId") int userId) {
        try {
            if (userId <= 0) { // Invalid userId check
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Invalid user ID format\"}")
                        .build();
            }

            List<Booking> history = userService.getBookingHistory(userId);
            if (history == null || history.isEmpty()) { // No history found
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"No booking history found for this user\"}")
                        .build();
            }

            return Response.ok(history).build();

        } catch (Exception e) { // Unexpected error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}")
                    .build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@QueryParam("userId") int userId, @QueryParam("password") String password, User user) {
        try {
            if (userId <= 0 || password == null || password.isEmpty()) { // Invalid input
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"User ID and password are required\"}")
                        .build();
            }

            boolean success = userService.updateUserInfo(userId, password, user);
            if (success) {
                return Response.ok("{\"message\": \"User updated successfully\"}").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid password or user not found\"}")
                        .build();
            }
        }  catch (Exception e) { // Unexpected error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}")
                    .build();
        }
    }

    @PUT
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@QueryParam("userId") int userId,
                                   @QueryParam("oldPassword") String oldPass,
                                   @QueryParam("newPassword") String newPass) {
        try {
            if (userId <= 0 || oldPass == null || oldPass.isEmpty() || newPass == null || newPass.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"User ID, old password, and new password are required\"}")
                        .build();
            }

            boolean success = userService.changePassword(userId, oldPass, newPass);
            if (success) {
                return Response.ok("{\"message\": \"Password changed successfully\"}").build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Incorrect old password or user not found\"}")
                        .build();
            }
        } catch (Exception e) { // Unexpected error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}")
                    .build();
        }
    }
}
