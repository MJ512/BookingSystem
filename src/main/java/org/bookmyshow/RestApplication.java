package org.bookmyshow;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookmyshow.controller.MovieController;
import org.bookmyshow.repository.*;
import org.bookmyshow.repository.impl.*;
import org.bookmyshow.service.BookingService;
import org.bookmyshow.service.MovieService;
import org.bookmyshow.service.user.RegistrationService;
import org.bookmyshow.service.user.UserDashboardService;
import org.bookmyshow.service.user.UserLoginService;
import org.bookmyshow.controller.BookingController;
import org.bookmyshow.controller.user.RegistrationController;
import org.bookmyshow.controller.user.UserDashboardController;
import org.bookmyshow.controller.user.UserLoginController;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(BookingController.class, UserLoginController.class,
                RegistrationController.class, UserDashboardController.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new DependencyBinder());
    }

    private static class DependencyBinder extends AbstractBinder {
        @Override
        protected void configure() {

            bind(UserDAO.class).to(UserDAOInterface.class);
            bind(UserDashboardDAO.class).to(UserDashboardDAOInterface.class);
            bind(BookingDAO.class).to(BookingDAOInterface.class);
            bind(ShowDAO.class).to(ShowDAOInterface.class);
            bind(ValidationDAO.class).to(ValidationDAOInterface.class);
            bind(MovieDAO.class).to(MovieDAOInterface.class);

            // Bind services
            bindAsContract(UserLoginService.class);
            bindAsContract(UserDashboardService.class);
            bindAsContract(BookingService.class);
            bindAsContract(RegistrationService.class);
            bindAsContract(MovieService.class);

            // Bind controllers
            bindAsContract(UserLoginController.class);
            bindAsContract(UserDashboardController.class);
            bindAsContract(BookingController.class);
            bindAsContract(RegistrationController.class);
            bindAsContract(MovieController.class);
        }
    }

}
