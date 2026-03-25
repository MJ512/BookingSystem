package org.bookmyshow.controller.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookmyshow.model.User;
import org.bookmyshow.service.user.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * REST controller for user registration.
 */
@Path("/register")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    @Inject
    public RegistrationController(final RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * POST /register
     * Registers a new user. All fields are mandatory.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(final User user) {
        if (user == null || isBlank(user.getName()) || isBlank(user.getEmail())
                || isBlank(user.getPhone()) || isBlank(user.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"All fields (name, email, phone, password) are required\"}")
                    .build();
        }

        try {
            registrationService.registerUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Registration successful\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email or phone number already registered.\"}")
                        .build();
            }
            logger.error("Database error during registration.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"A database error occurred. Please try again later.\"}")
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Password hashing algorithm not available.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server configuration error.\"}")
                    .build();
        }
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
