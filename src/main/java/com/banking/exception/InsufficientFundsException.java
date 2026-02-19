package com.banking.exception;

/**
 * Exception thrown when an account has insufficient funds for an operation.
 */
public class InsufficientFundsException extends BankingException {
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
