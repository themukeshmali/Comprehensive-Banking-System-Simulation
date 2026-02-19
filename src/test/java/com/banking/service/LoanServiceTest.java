package com.banking.service;

import com.banking.exception.LoanException;
import com.banking.factory.AccountFactory;
import com.banking.model.Customer;
import com.banking.model.Loan;
import com.banking.model.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Loan Service Tests")
class LoanServiceTest {
    private BankingService bankingService;
    private LoanService loanService;
    private String customerId;

    @BeforeEach
    void setUp() {
        BankingService.resetInstance();
        bankingService = BankingService.getInstance();
        loanService = new LoanService(bankingService);

        Customer customer = bankingService.addCustomer("Test User", "test@test.com", "5555555555");
        customerId = customer.getCustomerId();
        // Create account with balance for eligibility
        bankingService.createAccount(customerId, AccountFactory.AccountType.SAVINGS, 50000.0);
    }

    @Test
    @DisplayName("Should apply for personal loan")
    void testApplyForLoan() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 24);
        assertNotNull(loan);
        assertNotNull(loan.getLoanId());
        assertEquals(Loan.LoanStatus.PENDING, loan.getStatus());
        assertEquals(100000.0, loan.getPrincipal(), 0.01);
        assertEquals(24, loan.getTermMonths());
    }

    @Test
    @DisplayName("Should calculate EMI correctly")
    void testEmiCalculation() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 12.0, 12);
        double emi = loan.getMonthlyPayment();
        // EMI for 100000 at 12% for 12 months ≈ 8884.88
        assertEquals(8884.88, emi, 1.0);
    }

    @Test
    @DisplayName("Should approve loan")
    void testApproveLoan() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 24);
        loanService.approveLoan(loan.getLoanId());
        assertEquals(Loan.LoanStatus.ACTIVE, loan.getStatus());
    }

    @Test
    @DisplayName("Should reject approving non-pending loan")
    void testApproveActiveLoan() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 24);
        loanService.approveLoan(loan.getLoanId());
        assertThrows(LoanException.class, () -> loanService.approveLoan(loan.getLoanId()));
    }

    @Test
    @DisplayName("Should make loan payment")
    void testMakePayment() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 24);
        loanService.approveLoan(loan.getLoanId());

        double payment = loanService.makePayment(loan.getLoanId());
        assertTrue(payment > 0);
        assertEquals(1, loan.getPaymentsMade());
        assertTrue(loan.getRemainingBalance() < 100000.0);
    }

    @Test
    @DisplayName("Should reject payment on pending loan")
    void testPaymentOnPendingLoan() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 100000.0, 24);
        assertThrows(LoanException.class, () -> loanService.makePayment(loan.getLoanId()));
    }

    @Test
    @DisplayName("Should make multiple payments")
    void testMultiplePayments() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 50000.0, 12);
        loanService.approveLoan(loan.getLoanId());

        double totalPaid = loanService.makeMultiplePayments(loan.getLoanId(), 3);
        assertTrue(totalPaid > 0);
        assertEquals(3, loan.getPaymentsMade());
    }

    @Test
    @DisplayName("Should close loan after full payment")
    void testFullPayment() {
        Loan loan = loanService.applyForLoan(customerId, LoanType.PERSONAL, 10000.0, 12.0, 6);
        loanService.approveLoan(loan.getLoanId());

        loanService.makeMultiplePayments(loan.getLoanId(), 6);
        assertEquals(Loan.LoanStatus.CLOSED, loan.getStatus());
        assertEquals(0.0, loan.getRemainingBalance(), 0.01);
    }

    @Test
    @DisplayName("Should check loan eligibility")
    void testEligibility() {
        // Customer has 50000 balance, needs 20% of requested amount
        assertTrue(loanService.checkEligibility(customerId, 100000.0)); // needs 20000, has 50000
        assertFalse(loanService.checkEligibility(customerId, 500000.0)); // needs 100000, has 50000
    }

    @Test
    @DisplayName("Should find loans for customer")
    void testGetLoansForCustomer() {
        loanService.applyForLoan(customerId, LoanType.PERSONAL, 50000.0, 12);
        loanService.applyForLoan(customerId, LoanType.HOME, 500000.0, 120);
        assertEquals(2, loanService.getLoansForCustomer(customerId).size());
    }

    @Test
    @DisplayName("Should throw for non-existent loan")
    void testFindNonExistentLoan() {
        assertThrows(LoanException.class, () -> loanService.findLoan("LOAN-XXXX"));
    }

    @Test
    @DisplayName("Should apply for different loan types")
    void testDifferentLoanTypes() {
        Loan personal = loanService.applyForLoan(customerId, LoanType.PERSONAL, 50000.0, 24);
        Loan home = loanService.applyForLoan(customerId, LoanType.HOME, 500000.0, 120);
        Loan auto = loanService.applyForLoan(customerId, LoanType.AUTO, 300000.0, 48);
        Loan education = loanService.applyForLoan(customerId, LoanType.EDUCATION, 200000.0, 60);

        assertEquals(LoanType.PERSONAL, personal.getLoanType());
        assertEquals(LoanType.HOME, home.getLoanType());
        assertEquals(LoanType.AUTO, auto.getLoanType());
        assertEquals(LoanType.EDUCATION, education.getLoanType());
    }
}
