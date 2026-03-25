package org.bookmyshow.validation;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input patterns (email, phone, password).
 * All methods are stateless and thread-safe.
 */
public final class PatternValidation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,6}$");

    // Combined password pattern: 8+ chars, uppercase, lowercase, digit, special char
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    private PatternValidation() {}

    public static boolean isValidEmail(final String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Password must be at least 8 characters and contain uppercase, lowercase,
     * a digit, and a special character.
     */
    public static boolean isValidPassword(final String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /** Phone must be exactly 10 digits. */
    public static boolean isValidNumber(final String number) {
        return number != null && PHONE_PATTERN.matcher(number).matches();
    }
}
