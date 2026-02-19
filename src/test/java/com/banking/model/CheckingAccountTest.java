package com.banking.model;

import com.banking.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checking Account Tests")
class CheckingAccountTest {
    private CheckingAccount account;

    @BeforeEach
    void setUp() {
        account = new CheckingAccount("ACC-010", "Alice Smith", 5000.0);
    }

    @Test
    @DisplayName("Should create account with default overdraft")
    void testCreation() {
        assertEquals("ACC-010", account.getAccountId());
        assertEquals(5000.0, account.getBalance(), 0.01);
        assertEquals(5000.0, account.getOverdraftLimit(), 0.01);
        assertEquals("CHECKING", account.getAccountType());
    }

    @Test
    @DisplayName("Should deposit successfully")
    void testDeposit() {
        account.deposit(3000.0);
        assertEquals(8000.0, account.getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should withdraw within balance")
    void testWithdrawNormal() {
        account.withdraw(3000.0);
        assertEquals(2000.0, account.getBalance(), 0.01);
        assertFalse(account.isInOverdraft());
    }

    @Test
    @DisplayName("Should allow overdraft withdrawal")
    void testOverdraftWithdraw() {
        account.withdraw(7000.0);
        assertEquals(-2000.0, account.getBalance(), 0.01);
        assertTrue(account.isInOverdraft());
    }

    @Test
    @DisplayName("Should reject withdrawal exceeding overdraft")
    void testExcessiveWithdraw() {
        // Balance 5000 + overdraft 5000 = 10000 available
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(11000.0));
    }

    @Test
    @DisplayName("Should calculate available balance including overdraft")
    void testAvailableBalance() {
        assertEquals(10000.0, account.getAvailableBalance(), 0.01);
    }

    @Test
    @DisplayName("Should charge overdraft fee when negative")
    void testOverdraftFee() {
        account.withdraw(7000.0); // Balance = -2000
        double interest = account.calculateInterest();
        assertEquals(30.0, interest, 0.01); // 1.5% of 2000
    }

    @Test
    @DisplayName("Should return zero interest when positive balance")
    void testNoInterestPositiveBalance() {
        assertEquals(0.0, account.calculateInterest(), 0.01);
    }

    @Test
    @DisplayName("Should create account with custom overdraft")
    void testCustomOverdraft() {
        CheckingAccount custom = new CheckingAccount("ACC-011", "Bob", 1000.0, 10000.0);
        assertEquals(10000.0, custom.getOverdraftLimit());
        assertEquals(11000.0, custom.getAvailableBalance());
    }
}
