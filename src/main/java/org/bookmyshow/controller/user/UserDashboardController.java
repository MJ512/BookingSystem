package org.bookmyshow.controller.user;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookmyshow.model.Booking;
import org.bookmyshow.model.User;
import org.bookmyshow.service.user.UserDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * REST controller for user dashboard operations:
 * booking history, profile update, password change, logout.
 */
@Path("/users")
public class UserDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardController.class);

    private final UserDashboardService userService;

    @Inject
    public UserDashboardController(final UserDashboardService userService) {
        this.userService = userService;
    }

    /** GET /users/history/{user_id} */
    @GET
    @Path("/history/{user_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingHistory(@PathParam("user_id") final int userId) {
        if (userId <= 0) {
            return badRequest("Invalid user ID.");
        }
        try {
            List<Booking> history = userService.getBookingHistory(userId);
            if (history.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"No booking history found for this user.\"}")
                        .build();
            }
            return Response.ok(history).build();
        } catch (Exception e) {
            logger.error("Error fetching booking history for user {}.", userId, e);
            return internalError();
        }
    }

    /** PUT /users/update?user_id=&password= */
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@QueryParam("user_id") final int userId,
                               @QueryParam("password") final String password,
                               final User user) {
        if (userId <= 0 || isBlank(password)) {
            return badRequest("User ID and current password are required.");
        }
        try {
            if (userService.updateUserInfo(userId, password, user)) {
                return Response.ok("{\"message\": \"Profile updated successfully.\"}").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Incorrect password or user not found.\"}").build();
        } catch (Exception e) {
            logger.error("Error updating user {}.", userId, e);
            return internalError();
        }
    }

    /** PUT /users/change-password?user_id=&oldPassword=&newPassword= */
    @PUT
    @Path("/change-password")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@QueryParam("user_id") final int userId,
                                   @QueryParam("oldPassword") final String oldPass,
                                   @QueryParam("newPassword") final String newPass) {
        if (userId <= 0 || isBlank(oldPass) || isBlank(newPass)) {
            return badRequest("User ID, old password, and new password are all required.");
        }
        try {
            if (userService.changePassword(userId, oldPass, newPass)) {
                return Response.ok("{\"message\": \"Password changed successfully.\"}").build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Incorrect current password or user not found.\"}").build();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing password for user {}.", userId, e);
            return internalError();
        }
    }

    /** POST /users/logout */
    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return Response.ok("{\"message\": \"Logged out successfully.\"}").build();
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private boolean isBlank(final String s) { return s == null || s.isBlank(); }

    private Response badRequest(final String msg) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + msg + "\"}").build();
    }

    private Response internalError() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An unexpected error occurred. Please try again later.\"}").build();
    }
}
