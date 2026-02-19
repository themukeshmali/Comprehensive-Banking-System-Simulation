package com.banking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a bank customer with personal information and associated accounts.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private final List<Account> accounts;

    public Customer(String customerId, String name, String email, String phone) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = "";
        this.accounts = new ArrayList<>();
    }

    public Customer(String customerId, String name, String email, String phone, String address) {
        this(customerId, name, email, phone);
        this.address = address;
    }

    /**
     * Adds an account to this customer.
     */
    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (accounts.stream().anyMatch(a -> a.getAccountId().equals(account.getAccountId()))) {
            throw new IllegalArgumentException(
                    "Account " + account.getAccountId() + " already exists for this customer");
        }
        accounts.add(account);
    }

    /**
     * Removes an account from this customer.
     */
    public boolean removeAccount(String accountId) {
        return accounts.removeIf(a -> a.getAccountId().equals(accountId));
    }

    /**
     * Finds an account by ID.
     */
    public Account getAccount(String accountId) {
        return accounts.stream()
                .filter(a -> a.getAccountId().equals(accountId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns total balance across all accounts.
     */
    public double getTotalBalance() {
        return accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    /**
     * Returns the number of accounts this customer holds.
     */
    public int getAccountCount() {
        return accounts.size();
    }

    // --- Getters & Setters ---

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    // --- Object Overrides ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return String.format("Customer: %s | Name: %s | Email: %s | Phone: %s | Accounts: %d | Total Balance: ₹%.2f",
                customerId, name, email, phone, accounts.size(), getTotalBalance());
    }
}
