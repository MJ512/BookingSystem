package org.bookmyshow.validation;

import java.util.regex.Pattern;

public class Validation {

    private static final String LENGTH_PATTERN = ".{8,}"; // At least 8 characters
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*"; // At least 1 uppercase letter
    private static final String LOWERCASE_PATTERN = ".*[a-z].*"; // At least 1 lowercase letter
    private static final String NUMBER_PATTERN = ".*[0-9].*"; // At least 1 number
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"; // At least 1 special character

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    public final static boolean isValidNumber(String number) {

        if (number == null || number.length() != 10) {
            return false;
        }

        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    public final static boolean isValidPassword(String password) {
        return password != null &&
                Pattern.matches(LENGTH_PATTERN, password) &&
                Pattern.matches(UPPERCASE_PATTERN, password) &&
                Pattern.matches(LOWERCASE_PATTERN, password) &&
                Pattern.matches(NUMBER_PATTERN, password) &&
                Pattern.matches(SPECIAL_CHAR_PATTERN, password);
    }

    public final static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

}
