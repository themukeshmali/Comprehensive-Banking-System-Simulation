package com.banking.exception;

/**
 * Exception thrown for loan-related errors.
 */
public class LoanException extends BankingException {

    public LoanException(String message) {
        super(message);
    }

    public LoanException(String message, Throwable cause) {
        super(message, cause);
    }
}
