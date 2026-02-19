package com.banking.exception;

/**
 * Exception thrown when an account is not found or is invalid.
 */
public class InvalidAccountException extends BankingException {

    public InvalidAccountException(String message) {
        super(message);
    }

    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
