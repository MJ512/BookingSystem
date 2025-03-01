package org.bookingsystemapi;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.bookingsystemapi.dao.BookingDAO;
import org.bookingsystemapi.dao.ShowDAO;
import org.bookingsystemapi.dao.ValidationDAO;
import org.bookingsystemapi.service.BookingService;
import org.bookingsystemapi.servlet.BookingServlet;
import org.bookingsystemapi.servlet.RegistrationServlet;
import org.bookingsystemapi.servlet.UserDashboardServlet;
import org.bookingsystemapi.servlet.UserLoginServlet;
import org.bookingsystemapi.validation.BookingValidator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import java.util.Set;

@ApplicationPath("/api")  // Base path for REST API
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(BookingServlet.class, UserLoginServlet.class,
                RegistrationServlet.class, UserDashboardServlet.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new DependencyBinder());  // ✅ Register dependency injection
    }

    private static class DependencyBinder extends AbstractBinder {
        @Override
        protected void configure() {
            try (ScanResult scanResult = new ClassGraph()
                    .acceptPackages("org.bookingsystemapi")
                    .enableClassInfo()
                    .scan()) {

                scanResult.getAllClasses().forEach(classInfo -> {
                    try {
                        Class<?> clazz = Class.forName(classInfo.getName());
                        bindAsContract(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
