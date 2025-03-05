package org.bookmyshow.servlet;

import java.sql.SQLException;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;

import org.bookmyshow.model.User;
import org.bookmyshow.service.UserLoginService;

@Path("/login")
public class UserLoginServlet {

    private final UserLoginService userLoginService;

    @Inject
    private UserLoginServlet(UserLoginService userLoginService){
        this.userLoginService = userLoginService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(User loginRequest, @Context HttpServletRequest request) {

        final String loginInput = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (loginInput == null || loginInput.isEmpty() ||
                password == null || password.isEmpty()) {
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
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Database is temporarily unavailable. Please try again later.\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Unexpected error occurred\"}")
                    .build();
        }
    }
}
