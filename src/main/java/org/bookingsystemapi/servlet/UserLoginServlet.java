package org.bookingsystemapi.servlet;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;

import org.bookingsystemapi.model.User;
import org.bookingsystemapi.service.UserLoginService;

@Path("/login")
public class UserLoginServlet {

    private final UserLoginService userLoginService = new UserLoginService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(User loginRequest, @Context HttpServletRequest request) {

        String loginInput = loginRequest.getEmail();  // Can be email or phone number
        String password = loginRequest.getPassword();

        if (loginInput == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email/Phone and Password are required\"}")
                    .build();
        }

        try {
            User user = userLoginService.authenticateUser(loginInput, password);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid email/phone or password\"}")
                        .build();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("id", user.getUserId());

            String successMessage = "{\"message\": \"Login Successful. Welcome, " + user.getName() + "\"}";
            return Response.ok(successMessage).build();

        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Database error occurred\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}")
                    .build();
        }
    }
}
