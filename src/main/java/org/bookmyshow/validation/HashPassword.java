package org.bookmyshow.validation;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashPassword {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public final static String hashPassword(String password){
        return encoder.encode(password);
    }

    public final static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
