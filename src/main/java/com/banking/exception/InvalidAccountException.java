package com.banking.exception;

/**
 * Exception thrown when an account is not found or is invalid.
 */
public class InvalidAccountException extends BankingException {
    private static final long serialVersionUID = 1L;

    public InvalidAccountException(String message) {
        super(message);
    }

    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
