package com.banking.exception;

/**
 * Base exception for all banking-related errors.
 */
public class BankingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BankingException(String message) {
        super(message);
    }

    public BankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
