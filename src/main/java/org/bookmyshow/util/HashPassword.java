package org.bookmyshow.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashPassword {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hashPassword(final String password){
        return encoder.encode(password);
    }

    public static boolean verifyPassword(final String rawPassword, final String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
