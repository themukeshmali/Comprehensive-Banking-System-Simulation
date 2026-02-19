package com.banking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Tests")
class CustomerTest {
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("CUST-001", "John Doe", "john@example.com", "9876543210");
    }

    @Test
    @DisplayName("Should create customer with correct values")
    void testCreation() {
        assertEquals("CUST-001", customer.getCustomerId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals("9876543210", customer.getPhone());
        assertEquals(0, customer.getAccountCount());
    }

    @Test
    @DisplayName("Should add account to customer")
    void testAddAccount() {
        SavingsAccount account = new SavingsAccount("ACC-001", "John Doe", 5000);
        customer.addAccount(account);
        assertEquals(1, customer.getAccountCount());
        assertNotNull(customer.getAccount("ACC-001"));
    }

    @Test
    @DisplayName("Should reject duplicate account")
    void testDuplicateAccount() {
        SavingsAccount account = new SavingsAccount("ACC-001", "John Doe", 5000);
        customer.addAccount(account);
        assertThrows(IllegalArgumentException.class, () -> customer.addAccount(account));
    }

    @Test
    @DisplayName("Should remove account")
    void testRemoveAccount() {
        SavingsAccount account = new SavingsAccount("ACC-001", "John Doe", 5000);
        customer.addAccount(account);
        assertTrue(customer.removeAccount("ACC-001"));
        assertEquals(0, customer.getAccountCount());
    }

    @Test
    @DisplayName("Should calculate total balance across accounts")
    void testTotalBalance() {
        customer.addAccount(new SavingsAccount("ACC-001", "John Doe", 5000));
        customer.addAccount(new CheckingAccount("ACC-002", "John Doe", 3000));
        assertEquals(8000.0, customer.getTotalBalance(), 0.01);
    }

    @Test
    @DisplayName("Should find account by ID")
    void testFindAccount() {
        SavingsAccount account = new SavingsAccount("ACC-001", "John Doe", 5000);
        customer.addAccount(account);
        assertEquals(account, customer.getAccount("ACC-001"));
        assertNull(customer.getAccount("ACC-999"));
    }

    @Test
    @DisplayName("Should reject null customer ID")
    void testNullCustomerId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(null, "Test", "test@test.com", "1234567890"));
    }

    @Test
    @DisplayName("Should reject blank name")
    void testBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer("CUST-002", "  ", "test@test.com", "1234567890"));
    }

    @Test
    @DisplayName("Should create customer with address")
    void testCustomerWithAddress() {
        Customer c = new Customer("CUST-002", "Jane Doe", "jane@test.com", "1234567890", "123 Main St");
        assertEquals("123 Main St", c.getAddress());
    }

    @Test
    @DisplayName("Should update customer details")
    void testUpdateDetails() {
        customer.setName("John Updated");
        customer.setEmail("updated@test.com");
        assertEquals("John Updated", customer.getName());
        assertEquals("updated@test.com", customer.getEmail());
    }

    @Test
    @DisplayName("Should return unmodifiable accounts list")
    void testUnmodifiableList() {
        assertThrows(UnsupportedOperationException.class,
                () -> customer.getAccounts().add(new SavingsAccount("ACC-X", "Test", 100)));
    }
}
