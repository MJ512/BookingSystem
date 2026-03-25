package org.bookmyshow.exception;

/**
 * Thrown when a booking operation fails due to business rule violations
 * or unexpected database errors.
 */
public class BookingException extends RuntimeException {

    public BookingException(final String message) {
        super(message);
    }

    public BookingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
