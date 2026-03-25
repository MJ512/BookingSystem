package org.bookmyshow.controller.user;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookmyshow.model.User;
import org.bookmyshow.service.user.UserLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * REST controller for user login.
 */
@Path("/login")
public class UserLoginController {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    private final UserLoginService userLoginService;

    @Inject
    public UserLoginController(final UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    /**
     * POST /login
     * Authenticates a user and starts a session on success.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(final User loginRequest, @Context final HttpServletRequest request) {
        final String loginInput = loginRequest.getEmail();
        final String password = loginRequest.getPassword();

        if (loginInput == null || loginInput.isBlank() || password == null || password.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email/phone and password are required\"}")
                    .build();
        }

        try {
            final User user = userLoginService.authenticateUser(loginInput, password);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid credentials\"}")
                        .build();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getUserId());

            return Response.ok("{\"message\": \"Login successful. Welcome, " + user.getName() + "\"}").build();

        } catch (SQLException e) {
            logger.error("Database error during login.", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\": \"Service temporarily unavailable. Please try again later.\"}")
                    .build();
        } catch (Exception e) {
            logger.error("Unexpected error during login.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected error occurred.\"}")
                    .build();
        }
    }
}
