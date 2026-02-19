package com.banking.model;

import java.io.Serializable;

/**
 * Enum representing different types of loans offered by the bank.
 */
public enum LoanType implements Serializable {
    PERSONAL("Personal Loan", 12.0, 60),
    HOME("Home Loan", 8.5, 360),
    AUTO("Auto Loan", 9.5, 84),
    EDUCATION("Education Loan", 7.0, 120);

    private final String displayName;
    private final double defaultInterestRate; // Annual percentage
    private final int maxTermMonths;

    LoanType(String displayName, double defaultInterestRate, int maxTermMonths) {
        this.displayName = displayName;
        this.defaultInterestRate = defaultInterestRate;
        this.maxTermMonths = maxTermMonths;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultInterestRate() {
        return defaultInterestRate;
    }

    public int getMaxTermMonths() {
        return maxTermMonths;
    }

    @Override
    public String toString() {
        return String.format("%s (Rate: %.1f%%, Max Term: %d months)", displayName, defaultInterestRate, maxTermMonths);
    }
}
