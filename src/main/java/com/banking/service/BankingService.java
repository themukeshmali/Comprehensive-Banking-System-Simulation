package com.banking.service;

import com.banking.exception.InvalidAccountException;
import com.banking.factory.AccountFactory;
import com.banking.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Core banking service managing customers and accounts.
 * Implements the Singleton design pattern.
 */
public class BankingService {
    private static volatile BankingService instance;

    private final Map<String, Customer> customers;
    private final Map<String, Account> accounts;
    private final AtomicInteger customerIdCounter;
    private final AtomicInteger accountIdCounter;

    private BankingService() {
        this.customers = new LinkedHashMap<>();
        this.accounts = new LinkedHashMap<>();
        this.customerIdCounter = new AtomicInteger(1000);
        this.accountIdCounter = new AtomicInteger(2000);
    }

    /**
     * Returns the singleton instance of BankingService.
     */
    public static BankingService getInstance() {
        if (instance == null) {
            synchronized (BankingService.class) {
                if (instance == null) {
                    instance = new BankingService();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static void resetInstance() {
        synchronized (BankingService.class) {
            instance = null;
        }
    }

    // ==================== Customer Operations ====================

    /**
     * Registers a new customer.
     */
    public Customer addCustomer(String name, String email, String phone) {
        String customerId = "CUST-" + customerIdCounter.incrementAndGet();
        Customer customer = new Customer(customerId, name, email, phone);
        customers.put(customerId, customer);
        return customer;
    }

    /**
     * Registers a new customer with address.
     */
    public Customer addCustomer(String name, String email, String phone, String address) {
        String customerId = "CUST-" + customerIdCounter.incrementAndGet();
        Customer customer = new Customer(customerId, name, email, phone, address);
        customers.put(customerId, customer);
        return customer;
    }

    /**
     * Finds a customer by ID.
     */
    public Customer findCustomer(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new InvalidAccountException("Customer not found: " + customerId);
        }
        return customer;
    }

    /**
     * Searches customers by name (partial match).
     */
    public List<Customer> searchCustomersByName(String name) {
        return customers.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all customers.
     */
    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customers.values());
    }

    /**
     * Returns the total number of customers.
     */
    public int getCustomerCount() {
        return customers.size();
    }

    // ==================== Account Operations ====================

    /**
     * Creates a new account for a customer using the Factory pattern.
     */
    public Account createAccount(String customerId, AccountFactory.AccountType type, double initialBalance) {
        Customer customer = findCustomer(customerId);
        String accountId = "ACC-" + accountIdCounter.incrementAndGet();
        Account account = AccountFactory.createAccount(type, accountId, customer.getName(), initialBalance);
        accounts.put(accountId, account);
        customer.addAccount(account);
        return account;
    }

    /**
     * Finds an account by ID.
     */
    public Account findAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found: " + accountId);
        }
        return account;
    }

    /**
     * Returns all accounts.
     */
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    /**
     * Returns accounts filtered by type.
     */
    public List<Account> getAccountsByType(String type) {
        return accounts.values().stream()
                .filter(a -> a.getAccountType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Closes an account (sets inactive).
     */
    public void closeAccount(String accountId) {
        Account account = findAccount(accountId);
        if (account.getBalance() > 0) {
            System.out.printf("⚠ Account %s closed with remaining balance of ₹%.2f%n",
                    accountId, account.getBalance());
        }
        account.setActive(false);
    }

    /**
     * Returns total number of accounts.
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Returns total deposits across all accounts.
     */
    public double getTotalDeposits() {
        return accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    // ==================== Data Management ====================

    /**
     * Returns all data maps for persistence.
     */
    public Map<String, Customer> getCustomerMap() {
        return customers;
    }

    public Map<String, Account> getAccountMap() {
        return accounts;
    }

    /**
     * Loads data from persistence.
     */
    public void loadCustomers(Map<String, Customer> loadedCustomers) {
        customers.clear();
        customers.putAll(loadedCustomers);
        // Rebuild accounts map from customers
        accounts.clear();
        for (Customer customer : customers.values()) {
            for (Account account : customer.getAccounts()) {
                accounts.put(account.getAccountId(), account);
            }
        }
        // Update counters
        int maxCustId = customers.keySet().stream()
                .map(id -> id.replace("CUST-", ""))
                .mapToInt(Integer::parseInt)
                .max().orElse(1000);
        int maxAccId = accounts.keySet().stream()
                .map(id -> id.replace("ACC-", ""))
                .mapToInt(Integer::parseInt)
                .max().orElse(2000);
        customerIdCounter.set(maxCustId);
        accountIdCounter.set(maxAccId);
    }

    // ==================== Summary ====================

    /**
     * Prints a bank summary.
     */
    public void printBankSummary() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║            🏦 BANK SUMMARY                    ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.printf("║  Total Customers:     %d%n", getCustomerCount());
        System.out.printf("║  Total Accounts:      %d%n", getAccountCount());
        System.out.printf("║  Total Deposits:      ₹%.2f%n", getTotalDeposits());
        System.out.printf("║  Savings Accounts:    %d%n", getAccountsByType("SAVINGS").size());
        System.out.printf("║  Checking Accounts:   %d%n", getAccountsByType("CHECKING").size());
        System.out.printf("║  Fixed Deposits:      %d%n", getAccountsByType("FIXED_DEPOSIT").size());
        System.out.println("╚═══════════════════════════════════════════════╝\n");
    }
}
