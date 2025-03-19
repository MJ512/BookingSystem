package org.bookmyshow;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookmyshow.controller.MovieController;
import org.bookmyshow.repository.*;
import org.bookmyshow.repository.impl.*;
import org.bookmyshow.service.BookingService;
import org.bookmyshow.service.MovieService;
import org.bookmyshow.service.RegistrationService;
import org.bookmyshow.service.UserDashboardService;
import org.bookmyshow.service.UserLoginService;
import org.bookmyshow.controller.BookingController;
import org.bookmyshow.controller.RegistrationController;
import org.bookmyshow.controller.UserDashboardController;
import org.bookmyshow.controller.UserLoginController;
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

            bind(UserRepositoryImpl.class).to(UserRepository.class);
            bind(UserDashboardDAO.class).to(UserDashboardRepository.class);
            bind(BookingRepositoryImpl.class).to(BookingRepository.class);
            bind(MovieShowRepositoryImpl.class).to(ShowRepository.class);
            bind(ValidationRepositoryImpl.class).to(ValidationRepository.class);
            bind(MovieRepositoryImpl.class).to(MovieRepository.class);

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
