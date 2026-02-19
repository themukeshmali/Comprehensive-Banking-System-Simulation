package com.banking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all bank account types.
 * Provides common account functionality and enforces contract through abstract methods.
 */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String accountId;
    private String accountHolder;
    private double balance;
    private final LocalDateTime createdDate;
    private boolean active;
    private final List<Transaction> transactionHistory;

    protected Account(String accountId, String accountHolder, double initialBalance) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        if (accountHolder == null || accountHolder.isBlank()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountId = accountId;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        this.createdDate = LocalDateTime.now();
        this.active = true;
        this.transactionHistory = new ArrayList<>();
    }

    // --- Abstract Methods ---

    /**
     * Deposits the specified amount into the account.
     * @param amount the amount to deposit
     */
    public abstract void deposit(double amount);

    /**
     * Withdraws the specified amount from the account.
     * @param amount the amount to withdraw
     */
    public abstract void withdraw(double amount);

    /**
     * Calculates interest earned or owed on this account.
     * @return the calculated interest amount
     */
    public abstract double calculateInterest();

    /**
     * Returns the account type as a string.
     * @return account type identifier
     */
    public abstract String getAccountType();

    // --- Common Methods ---

    protected void creditBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.balance += amount;
    }

    protected void debitBalance(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        this.balance -= amount;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            this.transactionHistory.add(transaction);
        }
    }

    // --- Getters & Setters ---

    public String getAccountId() {
        return accountId;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // --- Object Overrides ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return String.format("[%s] Account: %s | Holder: %s | Balance: ₹%.2f | Created: %s | Active: %s",
                getAccountType(), accountId, accountHolder, balance,
                createdDate.format(FORMATTER), active);
    }
}
