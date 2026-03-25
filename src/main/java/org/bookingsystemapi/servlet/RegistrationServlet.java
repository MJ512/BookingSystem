package org.bookingsystemapi.servlet;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bookingsystemapi.model.User;
import org.bookingsystemapi.service.RegistrationService;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Path("/register")
public class RegistrationServlet {

    private final RegistrationService registrationService;

    @Inject
    public RegistrationServlet(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(User user){

        if(user.getName() == null || user.getName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPhone() == null || user.getPhone().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()){

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Require every field\"}").build();
        }

        try{
            registrationService.registerUser(user);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Registration successful\"}").build();

        } catch (SQLException e) {
            if (e.getMessage().contains("Email or Phone Number already exists.")) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Email or Phone Number already exists.\"}").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Database error occurred.\"}").build();
        } catch (NoSuchAlgorithmException e) {
           return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                   .entity("{\"error\": \"unexpected error\"}").build();
        }
    }
}
