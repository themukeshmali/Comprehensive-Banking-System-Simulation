package com.banking.strategy;

/**
 * Compound interest strategy for fixed deposit accounts.
 * Uses annual compounding with a higher rate.
 * Interest = P × (1 + r)^t - P
 */
public class FixedDepositInterestStrategy implements InterestStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateInterest(double principal, double annualRate, int periods) {
        if (principal <= 0 || annualRate <= 0 || periods <= 0) {
            return 0.0;
        }
        double rate = annualRate / 100.0;
        double amount = principal * Math.pow(1 + rate, periods);
        return amount - principal;
    }

    @Override
    public String getStrategyName() {
        return "Fixed Deposit Compound Interest (Annual)";
    }
}
