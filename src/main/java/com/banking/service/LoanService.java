package com.banking.service;

import com.banking.exception.LoanException;
import com.banking.model.Customer;
import com.banking.model.Loan;
import com.banking.model.Loan.LoanStatus;
import com.banking.model.LoanType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing loan applications, approvals, and payments.
 */
public class LoanService {
    private final BankingService bankingService;
    private final Map<String, Loan> loans;
    private static final double MIN_INCOME_RATIO = 3.0; // Income must be 3x EMI

    public LoanService(BankingService bankingService) {
        this.bankingService = bankingService;
        this.loans = new LinkedHashMap<>();
    }

    // ==================== Loan Application ====================

    /**
     * Applies for a new loan.
     */
    public Loan applyForLoan(String customerId, LoanType loanType, double principal, int termMonths) {
        // Verify customer exists
        Customer customer = bankingService.findCustomer(customerId);

        // Create loan with default rate for the type
        Loan loan = new Loan(customerId, loanType, principal, termMonths);
        loans.put(loan.getLoanId(), loan);

        System.out.printf("✅ Loan application submitted: %s%n", loan.getLoanId());
        System.out.printf("   Type: %s | Principal: ₹%.2f | Term: %d months%n",
                loanType.getDisplayName(), principal, termMonths);
        System.out.printf("   EMI: ₹%.2f | Total Payable: ₹%.2f%n",
                loan.getMonthlyPayment(), loan.getTotalPayable());

        return loan;
    }

    /**
     * Applies for a loan with a custom interest rate.
     */
    public Loan applyForLoan(String customerId, LoanType loanType, double principal,
            double interestRate, int termMonths) {
        Customer customer = bankingService.findCustomer(customerId);
        Loan loan = new Loan(customerId, loanType, principal, interestRate, termMonths);
        loans.put(loan.getLoanId(), loan);

        System.out.printf("✅ Loan application submitted: %s%n", loan.getLoanId());
        System.out.printf("   Type: %s | Principal: ₹%.2f | Rate: %.2f%% | Term: %d months%n",
                loanType.getDisplayName(), principal, interestRate, termMonths);
        System.out.printf("   EMI: ₹%.2f | Total Payable: ₹%.2f%n",
                loan.getMonthlyPayment(), loan.getTotalPayable());

        return loan;
    }

    // ==================== Loan Approval ====================

    /**
     * Approves a pending loan application.
     */
    public void approveLoan(String loanId) {
        Loan loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException("Loan " + loanId + " is not in PENDING status");
        }
        loan.approve();
        System.out.printf("✅ Loan %s approved and activated.%n", loanId);
    }

    // ==================== Loan Payment ====================

    /**
     * Makes a monthly payment on a loan.
     */
    public double makePayment(String loanId) {
        Loan loan = findLoan(loanId);
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new LoanException("Loan " + loanId + " is not active. Status: " + loan.getStatus());
        }

        double payment = loan.makePayment();
        System.out.printf("✅ Payment #%d of ₹%.2f applied to loan %s%n",
                loan.getPaymentsMade(), payment, loanId);
        System.out.printf("   Remaining balance: ₹%.2f%n", loan.getRemainingBalance());

        if (loan.getStatus() == LoanStatus.CLOSED) {
            System.out.printf("🎉 Loan %s is now fully paid off!%n", loanId);
        }

        return payment;
    }

    /**
     * Makes multiple monthly payments at once.
     */
    public double makeMultiplePayments(String loanId, int numberOfPayments) {
        double totalPaid = 0;
        for (int i = 0; i < numberOfPayments; i++) {
            Loan loan = findLoan(loanId);
            if (loan.getStatus() != LoanStatus.ACTIVE)
                break;
            totalPaid += makePayment(loanId);
        }
        return totalPaid;
    }

    // ==================== Eligibility Check ====================

    /**
     * Checks loan eligibility based on customer's total balance.
     */
    public boolean checkEligibility(String customerId, double requestedAmount) {
        Customer customer = bankingService.findCustomer(customerId);
        double totalBalance = customer.getTotalBalance();

        // Simple eligibility: customer must have at least 20% of loan amount as
        // deposits
        boolean eligible = totalBalance >= requestedAmount * 0.20;

        if (eligible) {
            System.out.printf("✅ Customer %s is eligible for a loan of ₹%.2f%n",
                    customerId, requestedAmount);
        } else {
            System.out.printf("❌ Customer %s is NOT eligible. Minimum deposit required: ₹%.2f (Current: ₹%.2f)%n",
                    customerId, requestedAmount * 0.20, totalBalance);
        }

        return eligible;
    }

    // ==================== Query Operations ====================

    /**
     * Finds a loan by ID.
     */
    public Loan findLoan(String loanId) {
        Loan loan = loans.get(loanId);
        if (loan == null) {
            throw new LoanException("Loan not found: " + loanId);
        }
        return loan;
    }

    /**
     * Returns all loans for a customer.
     */
    public List<Loan> getLoansForCustomer(String customerId) {
        return loans.values().stream()
                .filter(l -> l.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * Returns all active loans.
     */
    public List<Loan> getActiveLoans() {
        return loans.values().stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Returns all loans.
     */
    public Collection<Loan> getAllLoans() {
        return Collections.unmodifiableCollection(loans.values());
    }

    /**
     * Calculates total outstanding loan amount.
     */
    public double getTotalOutstandingLoans() {
        return loans.values().stream()
                .filter(l -> l.getStatus() == LoanStatus.ACTIVE)
                .mapToDouble(Loan::getRemainingBalance)
                .sum();
    }

    /**
     * Prints EMI schedule for a loan.
     */
    public void printAmortizationSchedule(String loanId) {
        Loan loan = findLoan(loanId);
        double balance = loan.getPrincipal();
        double monthlyRate = loan.getAnnualInterestRate() / 12.0 / 100.0;
        double emi = loan.getMonthlyPayment();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.printf("║  📊 AMORTIZATION SCHEDULE: %s%n", loanId);
        System.out.printf("║  Principal: ₹%.2f | Rate: %.2f%% | Term: %d months | EMI: ₹%.2f%n",
                loan.getPrincipal(), loan.getAnnualInterestRate(), loan.getTermMonths(), emi);
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  %-6s | %12s | %12s | %12s | %12s%n",
                "Month", "EMI", "Principal", "Interest", "Balance");
        System.out.println("║  ───────┼──────────────┼──────────────┼──────────────┼──────────────");

        double totalInterest = 0;
        for (int month = 1; month <= loan.getTermMonths() && balance > 0.01; month++) {
            double interest = balance * monthlyRate;
            double principal = emi - interest;
            if (principal > balance)
                principal = balance;
            balance -= principal;
            totalInterest += interest;

            if (month <= 12 || month == loan.getTermMonths() || month % 12 == 0) {
                System.out.printf("║  %-6d | ₹%10.2f | ₹%10.2f | ₹%10.2f | ₹%10.2f%n",
                        month, emi, principal, interest, Math.max(0, balance));
            } else if (month == 13) {
                System.out.println("║     ...   (showing first 12, yearly, and last month)");
            }
        }

        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Interest: ₹%.2f | Total Payable: ₹%.2f%n",
                totalInterest, loan.getPrincipal() + totalInterest);
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");
    }
}
