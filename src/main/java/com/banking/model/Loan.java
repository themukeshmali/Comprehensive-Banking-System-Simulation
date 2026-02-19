package com.banking.model;

import com.banking.strategy.InterestStrategy;
import com.banking.strategy.LoanInterestStrategy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a bank loan with EMI calculation and payment tracking.
 */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public enum LoanStatus {
        PENDING, APPROVED, ACTIVE, CLOSED, DEFAULTED
    }

    private final String loanId;
    private final String customerId;
    private final LoanType loanType;
    private final double principal;
    private final double annualInterestRate;
    private final int termMonths;
    private double monthlyPayment;
    private double remainingBalance;
    private double totalInterestPaid;
    private int paymentsMade;
    private LoanStatus status;
    private final LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private final InterestStrategy interestStrategy;

    public Loan(String customerId, LoanType loanType, double principal, int termMonths) {
        this(customerId, loanType, principal, loanType.getDefaultInterestRate(), termMonths);
    }

    public Loan(String customerId, LoanType loanType, double principal,
            double annualInterestRate, int termMonths) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Loan principal must be positive");
        }
        if (termMonths <= 0 || termMonths > loanType.getMaxTermMonths()) {
            throw new IllegalArgumentException(
                    String.format("Term must be between 1 and %d months for %s",
                            loanType.getMaxTermMonths(), loanType.getDisplayName()));
        }

        this.loanId = "LOAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.customerId = customerId;
        this.loanType = loanType;
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.termMonths = termMonths;
        this.remainingBalance = principal;
        this.totalInterestPaid = 0;
        this.paymentsMade = 0;
        this.status = LoanStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.interestStrategy = new LoanInterestStrategy();
        this.monthlyPayment = calculateEMI();
    }

    /**
     * Calculates Equated Monthly Installment (EMI).
     * EMI = P × r × (1 + r)^n / ((1 + r)^n - 1)
     */
    private double calculateEMI() {
        double monthlyRate = annualInterestRate / 12.0 / 100.0;
        if (monthlyRate == 0) {
            return principal / termMonths;
        }
        double factor = Math.pow(1 + monthlyRate, termMonths);
        return principal * monthlyRate * factor / (factor - 1);
    }

    /**
     * Makes a monthly payment on the loan.
     * 
     * @return the payment amount applied
     */
    public double makePayment() {
        if (status != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Can only make payments on active loans");
        }
        if (remainingBalance <= 0) {
            status = LoanStatus.CLOSED;
            return 0;
        }

        double payment = Math.min(monthlyPayment, remainingBalance + getMonthlyInterest());
        double interestComponent = getMonthlyInterest();
        double principalComponent = payment - interestComponent;

        remainingBalance -= principalComponent;
        totalInterestPaid += interestComponent;
        paymentsMade++;

        if (remainingBalance <= 0.01) {
            remainingBalance = 0;
            status = LoanStatus.CLOSED;
        }

        return payment;
    }

    /**
     * Calculates interest for the current month.
     */
    public double getMonthlyInterest() {
        return remainingBalance * (annualInterestRate / 12.0 / 100.0);
    }

    /**
     * Returns total amount payable over the loan term.
     */
    public double getTotalPayable() {
        return monthlyPayment * termMonths;
    }

    /**
     * Returns total interest payable.
     */
    public double getTotalInterest() {
        return getTotalPayable() - principal;
    }

    /**
     * Approve the loan application.
     */
    public void approve() {
        if (status != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending loans can be approved");
        }
        this.status = LoanStatus.ACTIVE;
        this.approvalDate = LocalDateTime.now();
    }

    // --- Getters ---

    public String getLoanId() {
        return loanId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public double getTotalInterestPaid() {
        return totalInterestPaid;
    }

    public int getPaymentsMade() {
        return paymentsMade;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Loan: %s | Type: %s | Principal: ₹%.2f | EMI: ₹%.2f | Rate: %.2f%% | " +
                        "Remaining: ₹%.2f | Payments: %d/%d | Status: %s",
                loanId, loanType.getDisplayName(), principal, monthlyPayment,
                annualInterestRate, remainingBalance, paymentsMade, termMonths, status);
    }
}
