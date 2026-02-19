package com.banking.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user inputs.
 */
public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?\\d{10,13}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]{2,50}$");

    /**
     * Validates that an amount is positive.
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0 && amount < 1_000_000_000; // Max 100 crore
    }

    /**
     * Validates that a string is not null or empty.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Validates an email address format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a phone number format.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

    /**
     * Validates a customer name.
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validates an account or customer ID format.
     */
    public static boolean isValidId(String id) {
        return id != null && !id.isBlank() && id.matches("^[A-Z]+-\\d+$");
    }

    /**
     * Validates interest rate (0-100%).
     */
    public static boolean isValidInterestRate(double rate) {
        return rate >= 0 && rate <= 100;
    }

    /**
     * Validates loan term in months.
     */
    public static boolean isValidTerm(int months) {
        return months > 0 && months <= 360;
    }

    /**
     * Parses a double from string, returns -1 if invalid.
     */
    public static double parseAmount(String input) {
        try {
            double amount = Double.parseDouble(input.trim());
            return isValidAmount(amount) ? amount : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses an integer from string, returns -1 if invalid.
     */
    public static int parseInt(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
