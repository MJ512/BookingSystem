package org.bookmyshow.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for hashing and verifying passwords using BCrypt.
 */
public final class HashPassword {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private HashPassword() {}

    public static String hashPassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank.");
        }
        return ENCODER.encode(password);
    }

    public static boolean verifyPassword(final String rawPassword, final String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return ENCODER.matches(rawPassword, hashedPassword);
    }
}
