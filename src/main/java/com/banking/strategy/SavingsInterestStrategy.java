package com.banking.strategy;

/**
 * Compound interest strategy for savings accounts.
 * Interest = P × (1 + r/n)^(n×t) - P
 * where n = compounding frequency (quarterly = 4)
 */
public class SavingsInterestStrategy implements InterestStrategy {
    private static final long serialVersionUID = 1L;
    private static final int COMPOUNDING_FREQUENCY = 4; // Quarterly

    @Override
    public double calculateInterest(double principal, double annualRate, int periods) {
        if (principal <= 0 || annualRate <= 0 || periods <= 0) {
            return 0.0;
        }
        double rate = annualRate / 100.0;
        double amount = principal * Math.pow(1 + rate / COMPOUNDING_FREQUENCY,
                COMPOUNDING_FREQUENCY * periods);
        return amount - principal;
    }

    @Override
    public String getStrategyName() {
        return "Savings Compound Interest (Quarterly)";
    }
}
