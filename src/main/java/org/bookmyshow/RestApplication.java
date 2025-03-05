package org.bookmyshow;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookmyshow.dao.*;
import org.bookmyshow.service.BookingService;
import org.bookmyshow.service.RegistrationService;
import org.bookmyshow.service.UserDashboardService;
import org.bookmyshow.service.UserLoginService;
import org.bookmyshow.servlet.BookingServlet;
import org.bookmyshow.servlet.RegistrationServlet;
import org.bookmyshow.servlet.UserDashboardServlet;
import org.bookmyshow.servlet.UserLoginServlet;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(BookingServlet.class, UserLoginServlet.class,
                RegistrationServlet.class, UserDashboardServlet.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new DependencyBinder());
    }

    private static class DependencyBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bindAsContract(UserDashboardDAO.class);
            bindAsContract(BookingDAO.class);
            bindAsContract(ShowDAO.class);
            bindAsContract(ValidationDAO.class);
            bindAsContract(SeatDAO.class);
            bindAsContract(UserDAO.class);
            bindAsContract(MovieDAO.class);

            bindAsContract(UserDashboardService.class);
            bindAsContract(BookingService.class);
            bindAsContract(UserLoginService.class);
            bindAsContract(RegistrationService.class);

            bindAsContract(UserDashboardServlet.class);
            bindAsContract(UserLoginServlet.class);
            bindAsContract(BookingServlet.class);
            bindAsContract(RegistrationServlet.class);
        }
    }
}
