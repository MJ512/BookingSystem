package org.bookingsystemapi;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookingsystemapi.dao.*;
import org.bookingsystemapi.service.BookingService;
import org.bookingsystemapi.service.RegistrationService;
import org.bookingsystemapi.service.UserDashboardService;
import org.bookingsystemapi.service.UserLoginService;
import org.bookingsystemapi.servlet.BookingServlet;
import org.bookingsystemapi.servlet.RegistrationServlet;
import org.bookingsystemapi.servlet.UserDashboardServlet;
import org.bookingsystemapi.servlet.UserLoginServlet;
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
