package com.banking.util;

import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Loan;
import com.banking.model.Transaction;
import com.banking.service.BankingService;
import com.banking.service.LoanService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for generating banking reports and statements.
 */
public class ReportGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "═══════════════════════════════════════════════════════════════════";

    /**
     * Generates and prints a comprehensive bank report.
     */
    public static void generateBankReport(BankingService bankingService, LoanService loanService) {
        System.out.println("\n╔" + SEPARATOR + "╗");
        System.out.println("║                    🏦 COMPREHENSIVE BANK REPORT                    ║");
        System.out.printf("║                    Generated: %s              ║%n",
                LocalDateTime.now().format(FORMATTER));
        System.out.println("╠" + SEPARATOR + "╣");

        // Customer Summary
        System.out.println("║  📋 CUSTOMER SUMMARY");
        System.out.printf("║    Total Customers: %d%n", bankingService.getCustomerCount());
        System.out.println("║");

        // Account Summary
        System.out.println("║  🏧 ACCOUNT SUMMARY");
        System.out.printf("║    Total Accounts:        %d%n", bankingService.getAccountCount());
        System.out.printf("║    Total Deposits:        ₹%.2f%n", bankingService.getTotalDeposits());
        System.out.printf("║    Savings Accounts:      %d%n", bankingService.getAccountsByType("SAVINGS").size());
        System.out.printf("║    Checking Accounts:     %d%n", bankingService.getAccountsByType("CHECKING").size());
        System.out.printf("║    Fixed Deposits:        %d%n", bankingService.getAccountsByType("FIXED_DEPOSIT").size());
        System.out.println("║");

        // Loan Summary
        System.out.println("║  💰 LOAN SUMMARY");
        System.out.printf("║    Total Loans:           %d%n", loanService.getAllLoans().size());
        System.out.printf("║    Active Loans:          %d%n", loanService.getActiveLoans().size());
        System.out.printf("║    Outstanding Amount:    ₹%.2f%n", loanService.getTotalOutstandingLoans());
        System.out.println("║");

        System.out.println("╚" + SEPARATOR + "╝\n");
    }

    /**
     * Generates a detailed customer report.
     */
    public static void generateCustomerReport(Customer customer) {
        System.out.println("\n╔" + SEPARATOR + "╗");
        System.out.printf("║  👤 CUSTOMER REPORT: %s%n", customer.getCustomerId());
        System.out.println("╠" + SEPARATOR + "╣");
        System.out.printf("║  Name:    %s%n", customer.getName());
        System.out.printf("║  Email:   %s%n", customer.getEmail());
        System.out.printf("║  Phone:   %s%n", customer.getPhone());
        System.out.printf("║  Address: %s%n", customer.getAddress().isEmpty() ? "N/A" : customer.getAddress());
        System.out.println("║");
        System.out.println("║  📊 ACCOUNTS:");

        if (customer.getAccounts().isEmpty()) {
            System.out.println("║    No accounts found.");
        } else {
            for (Account account : customer.getAccounts()) {
                System.out.printf("║    • %s%n", account);
            }
        }

        System.out.printf("║%n║  Total Balance: ₹%.2f%n", customer.getTotalBalance());
        System.out.println("╚" + SEPARATOR + "╝\n");
    }

    /**
     * Generates a loan summary report.
     */
    public static void generateLoanReport(Collection<Loan> loans) {
        System.out.println("\n╔" + SEPARATOR + "╗");
        System.out.println("║                       💰 LOAN REPORT                                ║");
        System.out.println("╠" + SEPARATOR + "╣");

        if (loans.isEmpty()) {
            System.out.println("║  No loans found.");
        } else {
            for (Loan loan : loans) {
                System.out.printf("║  %s%n", loan);
            }
        }

        double totalOutstanding = loans.stream()
                .filter(l -> l.getStatus() == Loan.LoanStatus.ACTIVE)
                .mapToDouble(Loan::getRemainingBalance)
                .sum();

        System.out.printf("║%n║  Total Outstanding: ₹%.2f%n", totalOutstanding);
        System.out.println("╚" + SEPARATOR + "╝\n");
    }

    /**
     * Generates a list of all customers and their accounts.
     */
    public static void generateCustomerList(Collection<Customer> customers) {
        System.out.println("\n╔" + SEPARATOR + "╗");
        System.out.println("║                    📋 ALL CUSTOMERS                                 ║");
        System.out.println("╠" + SEPARATOR + "╣");

        if (customers.isEmpty()) {
            System.out.println("║  No customers registered.");
        } else {
            for (Customer c : customers) {
                System.out.printf("║  %-10s | %-20s | %-25s | Accounts: %d | Balance: ₹%.2f%n",
                        c.getCustomerId(), c.getName(), c.getEmail(),
                        c.getAccountCount(), c.getTotalBalance());
            }
        }

        System.out.println("╚" + SEPARATOR + "╝\n");
    }
}
