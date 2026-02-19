package com.banking.model;

import com.banking.exception.BankingException;
import com.banking.strategy.FixedDepositInterestStrategy;
import com.banking.strategy.InterestStrategy;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Fixed Deposit account with maturity date and early withdrawal penalty.
 */
public class FixedDepositAccount extends Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final double DEFAULT_INTEREST_RATE = 7.0; // 7% per annum
    private static final double EARLY_WITHDRAWAL_PENALTY_RATE = 1.0; // 1% penalty
    private static final int DEFAULT_TERM_MONTHS = 12;

    private double annualInterestRate;
    private int termMonths;
    private LocalDateTime maturityDate;
    private boolean matured;
    private InterestStrategy interestStrategy;

    public FixedDepositAccount(String accountId, String accountHolder, double initialBalance) {
        super(accountId, accountHolder, initialBalance);
        this.annualInterestRate = DEFAULT_INTEREST_RATE;
        this.termMonths = DEFAULT_TERM_MONTHS;
        this.maturityDate = LocalDateTime.now().plusMonths(termMonths);
        this.matured = false;
        this.interestStrategy = new FixedDepositInterestStrategy();
    }

    public FixedDepositAccount(String accountId, String accountHolder, double initialBalance,
            double annualInterestRate, int termMonths) {
        super(accountId, accountHolder, initialBalance);
        this.annualInterestRate = annualInterestRate;
        this.termMonths = termMonths;
        this.maturityDate = LocalDateTime.now().plusMonths(termMonths);
        this.matured = false;
        this.interestStrategy = new FixedDepositInterestStrategy();
    }

    @Override
    public void deposit(double amount) {
        throw new BankingException("Cannot deposit into a Fixed Deposit account after creation");
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (!matured && LocalDateTime.now().isBefore(maturityDate)) {
            // Early withdrawal - apply penalty
            double penalty = getBalance() * (EARLY_WITHDRAWAL_PENALTY_RATE / 100.0);
            double availableAfterPenalty = getBalance() - penalty;
            if (amount > availableAfterPenalty) {
                throw new BankingException(
                        String.format("Cannot withdraw ₹%.2f. Available after early withdrawal penalty: ₹%.2f",
                                amount, availableAfterPenalty));
            }
            debitBalance(amount + penalty);
            System.out.printf("⚠ Early withdrawal penalty of ₹%.2f applied.%n", penalty);
        } else {
            if (amount > getBalance()) {
                throw new BankingException("Withdrawal amount exceeds balance");
            }
            debitBalance(amount);
        }
    }

    @Override
    public double calculateInterest() {
        double years = termMonths / 12.0;
        return interestStrategy.calculateInterest(getBalance(), annualInterestRate, (int) Math.ceil(years));
    }

    @Override
    public String getAccountType() {
        return "FIXED_DEPOSIT";
    }

    /**
     * Check and update maturity status.
     */
    public void checkMaturity() {
        if (!matured && LocalDateTime.now().isAfter(maturityDate)) {
            matured = true;
            double interest = calculateInterest();
            creditBalance(interest);
            System.out.printf("✅ Fixed Deposit matured! Interest of ₹%.2f credited.%n", interest);
        }
    }

    // --- Getters & Setters ---

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public boolean isMatured() {
        return matured;
    }

    public InterestStrategy getInterestStrategy() {
        return interestStrategy;
    }

    public void setInterestStrategy(InterestStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Rate: %.2f%% | Term: %d months | Maturity: %s | Matured: %s",
                annualInterestRate, termMonths, maturityDate.toLocalDate(), matured);
    }
}
