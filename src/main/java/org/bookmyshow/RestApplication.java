package org.bookmyshow;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookmyshow.controller.booking.BookingController;
import org.bookmyshow.controller.user.RegistrationController;
import org.bookmyshow.controller.user.UserDashboardController;
import org.bookmyshow.controller.user.UserLoginController;
import org.bookmyshow.repository.*;
import org.bookmyshow.repository.impl.*;
import org.bookmyshow.service.booking.BookingService;
import org.bookmyshow.service.user.RegistrationService;
import org.bookmyshow.service.user.UserDashboardService;
import org.bookmyshow.service.user.UserLoginService;
import org.bookmyshow.validation.BookingValidator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.util.Set;

/**
 * JAX-RS application entry point.
 * All API routes are prefixed with /api  (e.g. http://host/BookMyShow/api/login).
 */
@ApplicationPath("/api")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                BookingController.class,
                UserLoginController.class,
                RegistrationController.class,
                UserDashboardController.class
        );
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new DependencyBinder());
    }

    private static class DependencyBinder extends AbstractBinder {
        @Override
        protected void configure() {
            // ── Repositories ──────────────────────────────────────────────────
            bind(UserRepositoryImpl.class)
                    .to(UserRepository.class).to(UserDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(UserDashboardImpl.class)
                    .to(UserDashboardRepository.class).to(UserDashboardDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(BookingRepositoryImpl.class)
                    .to(BookingRepository.class).to(BookingDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(ShowDAO.class)
                    .to(ShowDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(ValidationRepositoryImpl.class)
                    .to(ValidationRepository.class).to(ValidationDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(MovieRepositoryImpl.class)
                    .to(MovieRepository.class).to(MovieDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);
            bind(SeatRepositoryImpl.class)
                    .to(SeatRepository.class).to(SeatDAOInterface.class)
                    .in(jakarta.inject.Singleton.class);

            // ── Validators ────────────────────────────────────────────────────
            bindAsContract(BookingValidator.class).in(jakarta.inject.Singleton.class);

            // ── Services ──────────────────────────────────────────────────────
            bindAsContract(UserLoginService.class).in(jakarta.inject.Singleton.class);
            bindAsContract(UserDashboardService.class).in(jakarta.inject.Singleton.class);
            bindAsContract(BookingService.class).in(jakarta.inject.Singleton.class);
            bindAsContract(RegistrationService.class).in(jakarta.inject.Singleton.class);

            // ── Controllers ───────────────────────────────────────────────────
            bindAsContract(UserLoginController.class);
            bindAsContract(UserDashboardController.class);
            bindAsContract(BookingController.class);
            bindAsContract(RegistrationController.class);
        }
    }
}
