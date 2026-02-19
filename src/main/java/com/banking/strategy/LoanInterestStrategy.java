package com.banking.strategy;

/**
 * Interest strategy for loan amortization.
 * Calculates total interest paid over the loan term using EMI formula.
 * Total Interest = (EMI × n) - P
 */
public class LoanInterestStrategy implements InterestStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateInterest(double principal, double annualRate, int termMonths) {
        if (principal <= 0 || annualRate <= 0 || termMonths <= 0) {
            return 0.0;
        }
        double monthlyRate = annualRate / 12.0 / 100.0;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        double emi = principal * monthlyRate * factor / (factor - 1);
        return (emi * termMonths) - principal;
    }

    @Override
    public String getStrategyName() {
        return "Loan Amortization Interest";
    }
}
