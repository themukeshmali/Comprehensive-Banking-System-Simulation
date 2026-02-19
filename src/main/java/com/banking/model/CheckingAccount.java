package com.banking.model;

import com.banking.exception.InsufficientFundsException;

import java.io.Serializable;

/**
 * Checking account with overdraft protection.
 */
public class CheckingAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final double DEFAULT_OVERDRAFT_LIMIT = 5000.0;

    private double overdraftLimit;

    public CheckingAccount(String accountId, String accountHolder, double initialBalance) {
        super(accountId, accountHolder, initialBalance);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public CheckingAccount(String accountId, String accountHolder, double initialBalance, double overdraftLimit) {
        super(accountId, accountHolder, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        creditBalance(amount);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (getBalance() + overdraftLimit < amount) {
            throw new InsufficientFundsException(
                    String.format("Withdrawal of ₹%.2f exceeds available funds (Balance: ₹%.2f + Overdraft: ₹%.2f)",
                            amount, getBalance(), overdraftLimit));
        }
        debitBalance(amount);
    }

    @Override
    public double calculateInterest() {
        // Checking accounts typically don't earn interest
        // But charge overdraft fee if balance is negative
        if (getBalance() < 0) {
            return Math.abs(getBalance()) * 0.015; // 1.5% overdraft fee
        }
        return 0.0;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }

    /**
     * Check if the account is currently in overdraft.
     */
    public boolean isInOverdraft() {
        return getBalance() < 0;
    }

    /**
     * Returns the available balance including overdraft.
     */
    public double getAvailableBalance() {
        return getBalance() + overdraftLimit;
    }

    // --- Getters & Setters ---

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Overdraft Limit: ₹%.2f | Available: ₹%.2f",
                overdraftLimit, getAvailableBalance());
    }
}
