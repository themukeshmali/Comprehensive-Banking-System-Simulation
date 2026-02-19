package com.banking;

import com.banking.exception.BankingException;
import com.banking.factory.AccountFactory;
import com.banking.model.*;
import com.banking.observer.FraudDetector;
import com.banking.observer.TransactionLogger;
import com.banking.service.BankingService;
import com.banking.service.LoanService;
import com.banking.service.TransactionService;
import com.banking.util.DataPersistence;
import com.banking.util.InputValidator;
import com.banking.util.ReportGenerator;

import java.util.Map;
import java.util.Scanner;

/**
 * Main Banking Application with interactive CLI menu.
 * Entry point for the Banking System Simulation.
 */
public class BankingApp {
    private final Scanner scanner;
    private final BankingService bankingService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final TransactionLogger transactionLogger;
    private final FraudDetector fraudDetector;

    public BankingApp() {
        this.scanner = new Scanner(System.in);
        this.bankingService = BankingService.getInstance();
        this.transactionService = new TransactionService(bankingService);
        this.loanService = new LoanService(bankingService);

        // Set up observers
        this.transactionLogger = new TransactionLogger();
        this.fraudDetector = new FraudDetector();
        transactionService.addObserver(transactionLogger);
        transactionService.addObserver(fraudDetector);
    }

    public static void main(String[] args) {
        BankingApp app = new BankingApp();
        app.printWelcomeBanner();
        app.loadSavedData();
        app.run();
    }

    /**
     * Main application loop.
     */
    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> customerMenu();
                    case 2 -> accountMenu();
                    case 3 -> transactionMenu();
                    case 4 -> loanMenu();
                    case 5 -> reportMenu();
                    case 6 -> saveData();
                    case 7 -> loadSavedData();
                    case 8 -> {
                        saveData();
                        running = false;
                        System.out.println("\n👋 Thank you for using Banking System. Goodbye!\n");
                    }
                    default -> System.out.println("❌ Invalid option. Please try again.");
                }
            } catch (BankingException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    // ==================== Menus ====================

    private void printMainMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       🏦 BANKING SYSTEM MENU          ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. 👤 Customer Management            ║");
        System.out.println("║  2. 🏧 Account Management             ║");
        System.out.println("║  3. 💸 Transactions                   ║");
        System.out.println("║  4. 💰 Loan Management                ║");
        System.out.println("║  5. 📊 Reports                        ║");
        System.out.println("║  6. 💾 Save Data                      ║");
        System.out.println("║  7. 📂 Load Data                      ║");
        System.out.println("║  8. 🚪 Exit                           ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }

    // ==================== Customer Menu ====================

    private void customerMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│     👤 CUSTOMER MANAGEMENT        │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│  1. Register New Customer         │");
        System.out.println("│  2. View Customer Details         │");
        System.out.println("│  3. Search Customers              │");
        System.out.println("│  4. List All Customers            │");
        System.out.println("│  5. Back to Main Menu             │");
        System.out.println("└───────────────────────────────────┘");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> registerCustomer();
            case 2 -> viewCustomer();
            case 3 -> searchCustomers();
            case 4 -> ReportGenerator.generateCustomerList(bankingService.getAllCustomers());
            case 5 -> {
            }
            default -> System.out.println("❌ Invalid option.");
        }
    }

    private void registerCustomer() {
        System.out.println("\n── Register New Customer ──");
        String name = readString("Full Name: ");
        if (!InputValidator.isValidName(name)) {
            System.out.println("❌ Invalid name. Use 2-50 alphabetic characters.");
            return;
        }

        String email = readString("Email: ");
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("❌ Invalid email format.");
            return;
        }

        String phone = readString("Phone: ");
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("❌ Invalid phone number. Use 10-13 digits.");
            return;
        }

        String address = readString("Address (optional, press Enter to skip): ");

        Customer customer;
        if (address.isBlank()) {
            customer = bankingService.addCustomer(name, email, phone);
        } else {
            customer = bankingService.addCustomer(name, email, phone, address);
        }

        System.out.printf("✅ Customer registered successfully! ID: %s%n", customer.getCustomerId());
    }

    private void viewCustomer() {
        String customerId = readString("Enter Customer ID: ").toUpperCase();
        Customer customer = bankingService.findCustomer(customerId);
        ReportGenerator.generateCustomerReport(customer);
    }

    private void searchCustomers() {
        String query = readString("Search by name: ");
        var results = bankingService.searchCustomersByName(query);
        if (results.isEmpty()) {
            System.out.println("ℹ No customers found matching: " + query);
        } else {
            System.out.printf("Found %d customer(s):%n", results.size());
            results.forEach(c -> System.out.println("  " + c));
        }
    }

    // ==================== Account Menu ====================

    private void accountMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│     🏧 ACCOUNT MANAGEMENT         │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│  1. Open New Account              │");
        System.out.println("│  2. View Account Details          │");
        System.out.println("│  3. Check Balance                 │");
        System.out.println("│  4. Close Account                 │");
        System.out.println("│  5. List All Accounts             │");
        System.out.println("│  6. Back to Main Menu             │");
        System.out.println("└───────────────────────────────────┘");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> openAccount();
            case 2 -> viewAccount();
            case 3 -> checkBalance();
            case 4 -> closeAccount();
            case 5 -> listAccounts();
            case 6 -> {
            }
            default -> System.out.println("❌ Invalid option.");
        }
    }

    private void openAccount() {
        System.out.println("\n── Open New Account ──");
        String customerId = readString("Customer ID: ").toUpperCase();

        System.out.println("Account Types:");
        System.out.println("  1. Savings Account");
        System.out.println("  2. Checking Account");
        System.out.println("  3. Fixed Deposit Account");
        int typeChoice = readInt("Select account type: ");

        AccountFactory.AccountType type = switch (typeChoice) {
            case 1 -> AccountFactory.AccountType.SAVINGS;
            case 2 -> AccountFactory.AccountType.CHECKING;
            case 3 -> AccountFactory.AccountType.FIXED_DEPOSIT;
            default -> {
                System.out.println("❌ Invalid account type.");
                yield null;
            }
        };

        if (type == null)
            return;

        double initialBalance = readDouble("Initial deposit amount: ₹");
        if (initialBalance < 0) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        Account account = bankingService.createAccount(customerId, type, initialBalance);
        System.out.printf("✅ Account created! ID: %s | Type: %s | Balance: ₹%.2f%n",
                account.getAccountId(), account.getAccountType(), account.getBalance());
    }

    private void viewAccount() {
        String accountId = readString("Enter Account ID: ").toUpperCase();
        Account account = bankingService.findAccount(accountId);
        System.out.println("\n" + account);
    }

    private void checkBalance() {
        String accountId = readString("Enter Account ID: ").toUpperCase();
        Account account = bankingService.findAccount(accountId);
        System.out.printf("%n💰 Account %s Balance: ₹%.2f%n", accountId, account.getBalance());
    }

    private void closeAccount() {
        String accountId = readString("Enter Account ID to close: ").toUpperCase();
        String confirm = readString("Are you sure? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            bankingService.closeAccount(accountId);
            System.out.printf("✅ Account %s has been closed.%n", accountId);
        } else {
            System.out.println("ℹ Operation cancelled.");
        }
    }

    private void listAccounts() {
        var accounts = bankingService.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("ℹ No accounts found.");
        } else {
            System.out.println("\n── All Accounts ──");
            accounts.forEach(a -> System.out.println("  " + a));
        }
    }

    // ==================== Transaction Menu ====================

    private void transactionMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│     💸 TRANSACTIONS                │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│  1. Deposit                       │");
        System.out.println("│  2. Withdraw                      │");
        System.out.println("│  3. Transfer                      │");
        System.out.println("│  4. Account Statement             │");
        System.out.println("│  5. View Audit Log                │");
        System.out.println("│  6. Fraud Detection Report        │");
        System.out.println("│  7. Back to Main Menu             │");
        System.out.println("└───────────────────────────────────┘");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> performDeposit();
            case 2 -> performWithdrawal();
            case 3 -> performTransfer();
            case 4 -> viewStatement();
            case 5 -> transactionLogger.printAuditLog();
            case 6 -> fraudDetector.printFlaggedReport();
            case 7 -> {
            }
            default -> System.out.println("❌ Invalid option.");
        }
    }

    private void performDeposit() {
        String accountId = readString("Account ID: ").toUpperCase();
        double amount = readDouble("Deposit amount: ₹");
        if (amount <= 0) {
            System.out.println("❌ Invalid amount.");
            return;
        }
        transactionService.deposit(accountId, amount);
    }

    private void performWithdrawal() {
        String accountId = readString("Account ID: ").toUpperCase();
        double amount = readDouble("Withdrawal amount: ₹");
        if (amount <= 0) {
            System.out.println("❌ Invalid amount.");
            return;
        }
        transactionService.withdraw(accountId, amount);
    }

    private void performTransfer() {
        String fromId = readString("From Account ID: ").toUpperCase();
        String toId = readString("To Account ID: ").toUpperCase();
        double amount = readDouble("Transfer amount: ₹");
        if (amount <= 0) {
            System.out.println("❌ Invalid amount.");
            return;
        }
        transactionService.transfer(fromId, toId, amount);
    }

    private void viewStatement() {
        String accountId = readString("Account ID: ").toUpperCase();
        transactionService.printAccountStatement(accountId);
    }

    // ==================== Loan Menu ====================

    private void loanMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│     💰 LOAN MANAGEMENT             │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│  1. Apply for Loan                │");
        System.out.println("│  2. Approve Loan                  │");
        System.out.println("│  3. Make Loan Payment             │");
        System.out.println("│  4. Check Loan Eligibility        │");
        System.out.println("│  5. View Loan Details             │");
        System.out.println("│  6. Amortization Schedule         │");
        System.out.println("│  7. View All Loans                │");
        System.out.println("│  8. Back to Main Menu             │");
        System.out.println("└───────────────────────────────────┘");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> applyForLoan();
            case 2 -> approveLoan();
            case 3 -> makeLoanPayment();
            case 4 -> checkLoanEligibility();
            case 5 -> viewLoanDetails();
            case 6 -> viewAmortizationSchedule();
            case 7 -> ReportGenerator.generateLoanReport(loanService.getAllLoans());
            case 8 -> {
            }
            default -> System.out.println("❌ Invalid option.");
        }
    }

    private void applyForLoan() {
        System.out.println("\n── Apply for Loan ──");
        String customerId = readString("Customer ID: ").toUpperCase();

        System.out.println("Loan Types:");
        LoanType[] types = LoanType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, types[i]);
        }
        int typeChoice = readInt("Select loan type: ");
        if (typeChoice < 1 || typeChoice > types.length) {
            System.out.println("❌ Invalid selection.");
            return;
        }
        LoanType loanType = types[typeChoice - 1];

        double principal = readDouble("Loan amount: ₹");
        if (principal <= 0) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        int term = readInt("Term (months, max " + loanType.getMaxTermMonths() + "): ");
        if (term <= 0 || term > loanType.getMaxTermMonths()) {
            System.out.println("❌ Invalid term.");
            return;
        }

        loanService.applyForLoan(customerId, loanType, principal, term);
    }

    private void approveLoan() {
        String loanId = readString("Loan ID to approve: ").toUpperCase();
        loanService.approveLoan(loanId);
    }

    private void makeLoanPayment() {
        String loanId = readString("Loan ID: ").toUpperCase();
        int payments = readInt("Number of payments to make: ");
        if (payments <= 0) {
            System.out.println("❌ Invalid number.");
            return;
        }
        loanService.makeMultiplePayments(loanId, payments);
    }

    private void checkLoanEligibility() {
        String customerId = readString("Customer ID: ").toUpperCase();
        double amount = readDouble("Requested loan amount: ₹");
        loanService.checkEligibility(customerId, amount);
    }

    private void viewLoanDetails() {
        String loanId = readString("Loan ID: ").toUpperCase();
        Loan loan = loanService.findLoan(loanId);
        System.out.println("\n" + loan);
    }

    private void viewAmortizationSchedule() {
        String loanId = readString("Loan ID: ").toUpperCase();
        loanService.printAmortizationSchedule(loanId);
    }

    // ==================== Report Menu ====================

    private void reportMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│     📊 REPORTS                     │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│  1. Bank Summary                  │");
        System.out.println("│  2. Customer Report               │");
        System.out.println("│  3. Loan Report                   │");
        System.out.println("│  4. All Customers List            │");
        System.out.println("│  5. Comprehensive Report          │");
        System.out.println("│  6. Back to Main Menu             │");
        System.out.println("└───────────────────────────────────┘");

        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1 -> bankingService.printBankSummary();
            case 2 -> {
                String id = readString("Customer ID: ").toUpperCase();
                ReportGenerator.generateCustomerReport(bankingService.findCustomer(id));
            }
            case 3 -> ReportGenerator.generateLoanReport(loanService.getAllLoans());
            case 4 -> ReportGenerator.generateCustomerList(bankingService.getAllCustomers());
            case 5 -> ReportGenerator.generateBankReport(bankingService, loanService);
            case 6 -> {
            }
            default -> System.out.println("❌ Invalid option.");
        }
    }

    // ==================== Data Persistence ====================

    private void saveData() {
        DataPersistence.saveCustomers(bankingService.getCustomerMap());
    }

    private void loadSavedData() {
        if (DataPersistence.dataExists()) {
            Map<String, Customer> loaded = DataPersistence.loadCustomers();
            if (!loaded.isEmpty()) {
                bankingService.loadCustomers(loaded);
            }
        }
    }

    // ==================== Input Helpers ====================

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ==================== Banner ====================

    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║           🏦  BANKING SYSTEM SIMULATION  🏦              ║");
        System.out.println("║                                                           ║");
        System.out.println("║     Account Management • Transactions • Loans             ║");
        System.out.println("║     Interest Calculations • Reports                       ║");
        System.out.println("║                                                           ║");
        System.out.println("║     Version 1.0  |  Java 17  |  Design Patterns          ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
