package com.eaut.footballclubmanagement.utils;

import java.util.regex.Pattern;

/** Mirrors the public authentication contract enforced by the backend. */
public final class CredentialValidator {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,32}$");
    private static final Pattern HAS_LETTER = Pattern.compile(".*[A-Za-z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*[0-9].*");

    private CredentialValidator() {
    }

    public static String usernameError(String value) {
        String username = value == null ? "" : value.trim();
        return USERNAME.matcher(username).matches()
                ? null
                : "Username must be 3-32 characters: letters, numbers, dot, underscore, or dash";
    }

    public static String passwordError(String value) {
        String password = value == null ? "" : value;
        if (password.length() < 8 || password.length() > 128) {
            return "Password must be 8 to 128 characters";
        }
        if (!HAS_LETTER.matcher(password).matches() || !HAS_DIGIT.matcher(password).matches()) {
            return "Password must contain at least one letter and one number";
        }
        return null;
    }
}
