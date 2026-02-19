package com.banking.model;

import com.banking.exception.InsufficientFundsException;
import com.banking.strategy.InterestStrategy;
import com.banking.strategy.SavingsInterestStrategy;

import java.io.Serializable;

/**
 * Savings account with minimum balance enforcement and compound interest.
 */
public class SavingsAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final double DEFAULT_MIN_BALANCE = 1000.0;
    private static final double DEFAULT_INTEREST_RATE = 4.0; // 4% per annum

    private double minimumBalance;
    private double annualInterestRate;
    private InterestStrategy interestStrategy;

    public SavingsAccount(String accountId, String accountHolder, double initialBalance) {
        super(accountId, accountHolder, initialBalance);
        this.minimumBalance = DEFAULT_MIN_BALANCE;
        this.annualInterestRate = DEFAULT_INTEREST_RATE;
        this.interestStrategy = new SavingsInterestStrategy();
    }

    public SavingsAccount(String accountId, String accountHolder, double initialBalance,
                          double minimumBalance, double annualInterestRate) {
        super(accountId, accountHolder, initialBalance);
        this.minimumBalance = minimumBalance;
        this.annualInterestRate = annualInterestRate;
        this.interestStrategy = new SavingsInterestStrategy();
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
        if (getBalance() - amount < minimumBalance) {
            throw new InsufficientFundsException(
                    String.format("Withdrawal of ₹%.2f would bring balance below minimum of ₹%.2f. Current balance: ₹%.2f",
                            amount, minimumBalance, getBalance()));
        }
        debitBalance(amount);
    }

    @Override
    public double calculateInterest() {
        return interestStrategy.calculateInterest(getBalance(), annualInterestRate, 1);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    /**
     * Apply calculated interest to the account balance.
     */
    public void applyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            creditBalance(interest);
        }
    }

    // --- Getters & Setters ---

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public InterestStrategy getInterestStrategy() {
        return interestStrategy;
    }

    public void setInterestStrategy(InterestStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Min Balance: ₹%.2f | Interest Rate: %.2f%%",
                minimumBalance, annualInterestRate);
    }
}
