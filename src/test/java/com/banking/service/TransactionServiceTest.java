package com.banking.service;

import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAccountException;
import com.banking.factory.AccountFactory;
import com.banking.model.Account;
import com.banking.model.Customer;
import com.banking.model.Transaction;
import com.banking.observer.FraudDetector;
import com.banking.observer.TransactionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transaction Service Tests")
class TransactionServiceTest {
    private BankingService bankingService;
    private TransactionService transactionService;
    private TransactionLogger logger;
    private FraudDetector fraudDetector;
    private String savingsAccountId;
    private String checkingAccountId;

    @BeforeEach
    void setUp() {
        BankingService.resetInstance();
        bankingService = BankingService.getInstance();
        transactionService = new TransactionService(bankingService);

        logger = new TransactionLogger();
        fraudDetector = new FraudDetector(50000.0);
        transactionService.addObserver(logger);
        transactionService.addObserver(fraudDetector);

        // Create test data
        Customer customer = bankingService.addCustomer("Test User", "test@test.com", "5555555555");
        Account savings = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.SAVINGS, 10000.0);
        Account checking = bankingService.createAccount(
                customer.getCustomerId(), AccountFactory.AccountType.CHECKING, 5000.0);
        savingsAccountId = savings.getAccountId();
        checkingAccountId = checking.getAccountId();
    }

    @Test
    @DisplayName("Should deposit successfully")
    void testDeposit() {
        Transaction txn = transactionService.deposit(savingsAccountId, 5000.0);
        assertNotNull(txn);
        assertEquals(Transaction.TransactionType.DEPOSIT, txn.getType());
        assertEquals(5000.0, txn.getAmount(), 0.01);
        assertEquals(15000.0, bankingService.findAccount(savingsAccountId).getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should withdraw successfully")
    void testWithdraw() {
        Transaction txn = transactionService.withdraw(savingsAccountId, 3000.0);
        assertNotNull(txn);
        assertEquals(Transaction.TransactionType.WITHDRAWAL, txn.getType());
        assertEquals(7000.0, bankingService.findAccount(savingsAccountId).getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should reject withdrawal exceeding available balance")
    void testInsufficientFunds() {
        assertThrows(InsufficientFundsException.class,
                () -> transactionService.withdraw(savingsAccountId, 50000.0));
    }

    @Test
    @DisplayName("Should transfer between accounts")
    void testTransfer() {
        Transaction txn = transactionService.transfer(savingsAccountId, checkingAccountId, 3000.0);
        assertNotNull(txn);
        assertEquals(Transaction.TransactionType.TRANSFER, txn.getType());
        assertEquals(7000.0, bankingService.findAccount(savingsAccountId).getBalance(), 0.01);
        assertEquals(8000.0, bankingService.findAccount(checkingAccountId).getBalance(), 0.01);
    }

    @Test
    @DisplayName("Should reject transfer to same account")
    void testTransferSameAccount() {
        assertThrows(InvalidAccountException.class,
                () -> transactionService.transfer(savingsAccountId, savingsAccountId, 1000.0));
    }

    @Test
    @DisplayName("Should notify observers on transaction")
    void testObserverNotification() {
        transactionService.deposit(savingsAccountId, 1000.0);
        assertEquals(1, logger.getAuditLog().size());
    }

    @Test
    @DisplayName("Should flag large transaction via fraud detector")
    void testFraudDetection() {
        transactionService.deposit(savingsAccountId, 100000.0);
        assertEquals(1, fraudDetector.getFlaggedCount());
    }

    @Test
    @DisplayName("Should not flag small transaction")
    void testNoFraudFlag() {
        transactionService.deposit(savingsAccountId, 1000.0);
        assertEquals(0, fraudDetector.getFlaggedCount());
    }

    @Test
    @DisplayName("Should track transaction count")
    void testTransactionCount() {
        transactionService.deposit(savingsAccountId, 1000.0);
        transactionService.withdraw(savingsAccountId, 500.0);
        transactionService.transfer(savingsAccountId, checkingAccountId, 200.0);
        assertEquals(3, transactionService.getTransactionCount());
    }

    @Test
    @DisplayName("Should track transaction history per account")
    void testAccountTransactionHistory() {
        transactionService.deposit(savingsAccountId, 1000.0);
        transactionService.deposit(savingsAccountId, 2000.0);
        transactionService.withdraw(savingsAccountId, 500.0);

        var history = transactionService.getTransactionsForAccount(savingsAccountId);
        assertEquals(3, history.size());
    }

    @Test
    @DisplayName("Should reject operations on inactive account")
    void testInactiveAccount() {
        bankingService.closeAccount(savingsAccountId);
        assertThrows(InvalidAccountException.class,
                () -> transactionService.deposit(savingsAccountId, 1000.0));
    }
}
