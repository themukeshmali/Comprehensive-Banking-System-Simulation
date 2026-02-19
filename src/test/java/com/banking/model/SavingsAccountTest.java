package com.banking.model;

import com.banking.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Savings Account Tests")
class SavingsAccountTest {
    private SavingsAccount account;

    @BeforeEach
    void setUp() {
        account = new SavingsAccount("ACC-001", "John Doe", 10000.0);
    }

    @Test
    @DisplayName("Should create account with correct initial values")
    void testAccountCreation() {
        assertEquals("ACC-001", account.getAccountId());
        assertEquals("John Doe", account.getAccountHolder());
        assertEquals(10000.0, account.getBalance(), 0.01);
        assertEquals("SAVINGS", account.getAccountType());
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("Should deposit successfully")
    void testDeposit() {
        account.deposit(5000.0);
        assertEquals(15000.0, account.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should reject negative deposit")
    void testNegativeDeposit() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(-100));
    }

    @Test
    @DisplayName("Should reject zero deposit")
    void testZeroDeposit() {
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
    }

    @Test
    @DisplayName("Should withdraw successfully")
    void testWithdraw() {
        account.withdraw(5000.0);
        assertEquals(5000.0, account.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should reject withdrawal below minimum balance")
    void testWithdrawBelowMinimum() {
        // Default min balance is 1000
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(9500.0));
    }

    @Test
    @DisplayName("Should reject negative withdrawal")
    void testNegativeWithdraw() {
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(-100));
    }

    @Test
    @DisplayName("Should calculate compound interest")
    void testInterestCalculation() {
        double interest = account.calculateInterest();
        assertTrue(interest > 0, "Interest should be positive");
        // 10000 at 4% quarterly compounding for 1 year ≈ 406.04
        assertEquals(406.04, interest, 1.0);
    }

    @Test
    @DisplayName("Should apply interest to balance")
    void testApplyInterest() {
        double balanceBefore = account.getBalance();
        account.applyInterest();
        assertTrue(account.getBalance() > balanceBefore);
    }

    @Test
    @DisplayName("Should create account with custom parameters")
    void testCustomParameters() {
        SavingsAccount custom = new SavingsAccount("ACC-002", "Jane Doe", 5000.0, 500.0, 6.0);
        assertEquals(500.0, custom.getMinimumBalance());
        assertEquals(6.0, custom.getAnnualInterestRate());
    }

    @Test
    @DisplayName("Should reject negative initial balance")
    void testNegativeInitialBalance() {
        assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount("ACC-003", "Test", -100));
    }

    @Test
    @DisplayName("Should have formatted toString")
    void testToString() {
        String str = account.toString();
        assertTrue(str.contains("SAVINGS"));
        assertTrue(str.contains("John Doe"));
        assertTrue(str.contains("ACC-001"));
    }
}
