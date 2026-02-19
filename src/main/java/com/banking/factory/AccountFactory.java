package com.banking.factory;

import com.banking.model.Account;
import com.banking.model.CheckingAccount;
import com.banking.model.FixedDepositAccount;
import com.banking.model.SavingsAccount;

/**
 * Factory class for creating different types of bank accounts.
 * Implements the Factory design pattern.
 */
public class AccountFactory {

    public enum AccountType {
        SAVINGS, CHECKING, FIXED_DEPOSIT
    }

    /**
     * Creates a new account of the specified type.
     *
     * @param type           the type of account to create
     * @param accountId      unique account identifier
     * @param accountHolder  name of the account holder
     * @param initialBalance initial deposit amount
     * @return the created Account instance
     * @throws IllegalArgumentException if the account type is unknown
     */
    public static Account createAccount(AccountType type, String accountId,
            String accountHolder, double initialBalance) {
        return switch (type) {
            case SAVINGS -> new SavingsAccount(accountId, accountHolder, initialBalance);
            case CHECKING -> new CheckingAccount(accountId, accountHolder, initialBalance);
            case FIXED_DEPOSIT -> new FixedDepositAccount(accountId, accountHolder, initialBalance);
        };
    }

    /**
     * Creates a savings account with custom parameters.
     */
    public static SavingsAccount createSavingsAccount(String accountId, String accountHolder,
            double initialBalance, double minBalance,
            double interestRate) {
        return new SavingsAccount(accountId, accountHolder, initialBalance, minBalance, interestRate);
    }

    /**
     * Creates a checking account with custom overdraft limit.
     */
    public static CheckingAccount createCheckingAccount(String accountId, String accountHolder,
            double initialBalance, double overdraftLimit) {
        return new CheckingAccount(accountId, accountHolder, initialBalance, overdraftLimit);
    }

    /**
     * Creates a fixed deposit with custom rate and term.
     */
    public static FixedDepositAccount createFixedDeposit(String accountId, String accountHolder,
            double initialBalance, double interestRate,
            int termMonths) {
        return new FixedDepositAccount(accountId, accountHolder, initialBalance, interestRate, termMonths);
    }
}
