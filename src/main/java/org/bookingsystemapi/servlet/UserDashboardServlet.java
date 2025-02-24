package org.bookingsystemapi.servlet;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.service.UserDashboardService;

@Path("/user")
public class UserDashboardServlet {
    private final UserDashboardService userService = new UserDashboardService();

    @GET
    @Path("/history/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookingHistory(@PathParam("userId") int userId) {
        return Response.ok(userService.getBookingHistory(userId)).build();
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@QueryParam("userId") int userId, @QueryParam("password") String password, User user) {
        boolean success = userService.updateUserInfo(userId, password, user);
        return success ? Response.ok("User updated successfully").build()
                : Response.status(Response.Status.UNAUTHORIZED).entity("Invalid password").build();
    }

    @PUT
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@QueryParam("userId") int userId, @QueryParam("oldPassword") String oldPass, @QueryParam("newPassword") String newPass) {
        boolean success = userService.changePassword(userId, oldPass, newPass);
        return success ? Response.ok("Password changed successfully").build()
                : Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect old password").build();
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(@QueryParam("emailOrPhone") String emailOrPhone) {
        boolean success = userService.sendPasswordResetLink(emailOrPhone);
        return success ? Response.ok("Password reset link sent successfully").build()
                : Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
    }
}