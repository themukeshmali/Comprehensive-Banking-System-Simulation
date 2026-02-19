package com.banking.service;

import com.banking.exception.InvalidAccountException;
import com.banking.factory.AccountFactory;
import com.banking.model.Account;
import com.banking.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Banking Service Tests")
class BankingServiceTest {

    private BankingService bankingService;

    @BeforeEach
    void setUp() {
        BankingService.resetInstance();
        bankingService = BankingService.getInstance();
    }

    @Test
    @DisplayName("Should return singleton instance")
    void testSingleton() {
        BankingService instance1 = BankingService.getInstance();
        BankingService instance2 = BankingService.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should add customer")
    void testAddCustomer() {
        Customer customer = bankingService.addCustomer("John Doe", "john@test.com", "9876543210");
        assertNotNull(customer);
        assertNotNull(customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals(1, bankingService.getCustomerCount());
    }

    @Test
    @DisplayName("Should add customer with address")
    void testAddCustomerWithAddress() {
        Customer customer = bankingService.addCustomer("Jane Doe", "jane@test.com", "1234567890", "123 Main St");
        assertEquals("123 Main St", customer.getAddress());
    }

    @Test
    @DisplayName("Should find customer by ID")
    void testFindCustomer() {
        Customer added = bankingService.addCustomer("Test User", "test@test.com", "5555555555");
        Customer found = bankingService.findCustomer(added.getCustomerId());
        assertEquals(added, found);
    }

    @Test
    @DisplayName("Should throw for non-existent customer")
    void testFindNonExistentCustomer() {
        assertThrows(InvalidAccountException.class,
                () -> bankingService.findCustomer("CUST-9999"));
    }

    @Test
    @DisplayName("Should search customers by name")
    void testSearchByName() {
        bankingService.addCustomer("Alice Smith", "alice@test.com", "1111111111");
        bankingService.addCustomer("Bob Johnson", "bob@test.com", "2222222222");
        bankingService.addCustomer("Alice Wonder", "alice2@test.com", "3333333333");

        var results = bankingService.searchCustomersByName("Alice");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Should create savings account")
    void testCreateSavingsAccount() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        Account account = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 5000.0);

        assertNotNull(account);
        assertEquals("SAVINGS", account.getAccountType());
        assertEquals(5000.0, account.getBalance(), 0.01);
        assertEquals(1, bankingService.getAccountCount());
    }

    @Test
    @DisplayName("Should create checking account")
    void testCreateCheckingAccount() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        Account account = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.CHECKING, 3000.0);
        assertEquals("CHECKING", account.getAccountType());
    }

    @Test
    @DisplayName("Should create fixed deposit account")
    void testCreateFixedDeposit() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        Account account = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.FIXED_DEPOSIT, 50000.0);
        assertEquals("FIXED_DEPOSIT", account.getAccountType());
    }

    @Test
    @DisplayName("Should find account by ID")
    void testFindAccount() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        Account created = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 5000.0);
        Account found = bankingService.findAccount(created.getAccountId());
        assertEquals(created, found);
    }

    @Test
    @DisplayName("Should close account")
    void testCloseAccount() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        Account account = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 5000.0);
        bankingService.closeAccount(account.getAccountId());
        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("Should calculate total deposits")
    void testTotalDeposits() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        bankingService.createAccount(customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 5000.0);
        bankingService.createAccount(customer.getCustomerId(), AccountFactory.AccountType.CHECKING, 3000.0);
        assertEquals(8000.0, bankingService.getTotalDeposits(), 0.01);
    }

    @Test
    @DisplayName("Should filter accounts by type")
    void testGetAccountsByType() {
        Customer customer = bankingService.addCustomer("Test", "test@test.com", "5555555555");
        bankingService.createAccount(customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 5000.0);
        bankingService.createAccount(customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 3000.0);
        bankingService.createAccount(customer.getCustomerId(), AccountFactory.AccountType.CHECKING, 2000.0);

        assertEquals(2, bankingService.getAccountsByType("SAVINGS").size());
        assertEquals(1, bankingService.getAccountsByType("CHECKING").size());
    }
}
