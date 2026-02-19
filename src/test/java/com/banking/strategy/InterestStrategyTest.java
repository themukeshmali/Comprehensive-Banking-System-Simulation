package com.banking.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Interest Strategy Tests")
class InterestStrategyTest {

    @Test
    @DisplayName("Savings strategy should calculate quarterly compound interest")
    void testSavingsInterest() {
        InterestStrategy strategy = new SavingsInterestStrategy();
        // 10000 at 4% quarterly compounding for 1 year
        double interest = strategy.calculateInterest(10000.0, 4.0, 1);
        // Expected: 10000 * (1 + 0.04/4)^4 - 10000 ≈ 406.04
        assertEquals(406.04, interest, 1.0);
        assertEquals("Savings Compound Interest (Quarterly)", strategy.getStrategyName());
    }

    @Test
    @DisplayName("Savings strategy should handle multiple years")
    void testSavingsMultipleYears() {
        InterestStrategy strategy = new SavingsInterestStrategy();
        double interest = strategy.calculateInterest(10000.0, 4.0, 3);
        assertTrue(interest > 1200); // More than simple interest of 1200
    }

    @Test
    @DisplayName("Savings strategy should return zero for zero principal")
    void testSavingsZeroPrincipal() {
        InterestStrategy strategy = new SavingsInterestStrategy();
        assertEquals(0.0, strategy.calculateInterest(0, 4.0, 1));
    }

    @Test
    @DisplayName("Savings strategy should return zero for negative rate")
    void testSavingsNegativeRate() {
        InterestStrategy strategy = new SavingsInterestStrategy();
        assertEquals(0.0, strategy.calculateInterest(10000, -1.0, 1));
    }

    @Test
    @DisplayName("Fixed deposit strategy should calculate annual compound interest")
    void testFixedDepositInterest() {
        InterestStrategy strategy = new FixedDepositInterestStrategy();
        // 100000 at 7% annual compounding for 1 year
        double interest = strategy.calculateInterest(100000.0, 7.0, 1);
        assertEquals(7000.0, interest, 1.0);
        assertEquals("Fixed Deposit Compound Interest (Annual)", strategy.getStrategyName());
    }

    @Test
    @DisplayName("Fixed deposit should compound over multiple years")
    void testFixedDepositCompounding() {
        InterestStrategy strategy = new FixedDepositInterestStrategy();
        // 100000 at 7% for 3 years
        double interest = strategy.calculateInterest(100000.0, 7.0, 3);
        // Expected: 100000 * (1.07)^3 - 100000 ≈ 22504.30
        assertEquals(22504.30, interest, 1.0);
    }

    @Test
    @DisplayName("Fixed deposit should return zero for zero periods")
    void testFixedDepositZeroPeriods() {
        InterestStrategy strategy = new FixedDepositInterestStrategy();
        assertEquals(0.0, strategy.calculateInterest(100000, 7.0, 0));
    }

    @Test
    @DisplayName("Loan strategy should calculate total interest via EMI")
    void testLoanInterest() {
        InterestStrategy strategy = new LoanInterestStrategy();
        // 100000 at 12% for 12 months
        double interest = strategy.calculateInterest(100000.0, 12.0, 12);
        // Total interest ≈ (EMI * 12) - 100000
        assertTrue(interest > 0);
        // EMI ≈ 8884.88, total = 106618.5, interest ≈ 6618.5
        assertEquals(6618.5, interest, 50.0);
        assertEquals("Loan Amortization Interest", strategy.getStrategyName());
    }

    @Test
    @DisplayName("Loan strategy should return zero for zero principal")
    void testLoanZeroPrincipal() {
        InterestStrategy strategy = new LoanInterestStrategy();
        assertEquals(0.0, strategy.calculateInterest(0, 12.0, 12));
    }

    @Test
    @DisplayName("Loan strategy should handle large amounts")
    void testLoanLargeAmount() {
        InterestStrategy strategy = new LoanInterestStrategy();
        double interest = strategy.calculateInterest(5000000.0, 8.5, 240);
        assertTrue(interest > 0);
        assertTrue(interest > 1000000); // Significant interest on large long-term loan
    }

    @Test
    @DisplayName("All strategies should handle same input differently")
    void testStrategiesDiffer() {
        InterestStrategy savings = new SavingsInterestStrategy();
        InterestStrategy fd = new FixedDepositInterestStrategy();
        InterestStrategy loan = new LoanInterestStrategy();

        double principal = 100000;
        double rate = 8.0;
        int periods = 12;

        double savingsInterest = savings.calculateInterest(principal, rate, periods);
        double fdInterest = fd.calculateInterest(principal, rate, periods);
        double loanInterest = loan.calculateInterest(principal, rate, periods);

        // All should give different results due to different formulas and period
        // interpretation
        assertNotEquals(savingsInterest, fdInterest, 0.01);
        // Savings uses quarterly compounding over years, FD uses annual, Loan uses
        // months
    }
}
