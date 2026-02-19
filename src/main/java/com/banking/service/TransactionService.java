package com.banking.service;

import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAccountException;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.Transaction.TransactionType;
import com.banking.observer.TransactionObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for processing banking transactions with observer notification.
 */
public class TransactionService {
    private final BankingService bankingService;
    private final List<Transaction> allTransactions;
    private final List<TransactionObserver> observers;

    public TransactionService(BankingService bankingService) {
        this.bankingService = bankingService;
        this.allTransactions = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    // ==================== Observer Management ====================

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(transaction);
        }
    }

    // ==================== Transaction Operations ====================

    /**
     * Deposits money into an account.
     */
    public Transaction deposit(String accountId, double amount) {
        Account account = bankingService.findAccount(accountId);
        if (!account.isActive()) {
            throw new InvalidAccountException("Account " + accountId + " is inactive");
        }

        account.deposit(amount);

        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT, amount, null, accountId,
                "Cash deposit", account.getBalance());
        account.addTransaction(transaction);
        allTransactions.add(transaction);
        notifyObservers(transaction);

        System.out.printf("✅ Deposited ₹%.2f to %s. New balance: ₹%.2f%n",
                amount, accountId, account.getBalance());
        return transaction;
    }

    /**
     * Withdraws money from an account.
     */
    public Transaction withdraw(String accountId, double amount) {
        Account account = bankingService.findAccount(accountId);
        if (!account.isActive()) {
            throw new InvalidAccountException("Account " + accountId + " is inactive");
        }

        account.withdraw(amount);

        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL, amount, accountId, null,
                "Cash withdrawal", account.getBalance());
        account.addTransaction(transaction);
        allTransactions.add(transaction);
        notifyObservers(transaction);

        System.out.printf("✅ Withdrew ₹%.2f from %s. New balance: ₹%.2f%n",
                amount, accountId, account.getBalance());
        return transaction;
    }

    /**
     * Transfers money between two accounts.
     */
    public Transaction transfer(String fromAccountId, String toAccountId, double amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidAccountException("Cannot transfer to the same account");
        }

        Account fromAccount = bankingService.findAccount(fromAccountId);
        Account toAccount = bankingService.findAccount(toAccountId);

        if (!fromAccount.isActive()) {
            throw new InvalidAccountException("Source account " + fromAccountId + " is inactive");
        }
        if (!toAccount.isActive()) {
            throw new InvalidAccountException("Target account " + toAccountId + " is inactive");
        }

        // Perform withdrawal then deposit
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction transaction = new Transaction(
                TransactionType.TRANSFER, amount, fromAccountId, toAccountId,
                String.format("Transfer from %s to %s", fromAccountId, toAccountId),
                fromAccount.getBalance());
        fromAccount.addTransaction(transaction);
        toAccount.addTransaction(transaction);
        allTransactions.add(transaction);
        notifyObservers(transaction);

        System.out.printf("✅ Transferred ₹%.2f from %s to %s%n", amount, fromAccountId, toAccountId);
        System.out.printf("   %s balance: ₹%.2f | %s balance: ₹%.2f%n",
                fromAccountId, fromAccount.getBalance(), toAccountId, toAccount.getBalance());
        return transaction;
    }

    // ==================== Query Operations ====================

    /**
     * Returns all transactions.
     */
    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(allTransactions);
    }

    /**
     * Returns transactions for a specific account.
     */
    public List<Transaction> getTransactionsForAccount(String accountId) {
        Account account = bankingService.findAccount(accountId);
        return account.getTransactionHistory();
    }

    /**
     * Returns the total number of transactions processed.
     */
    public int getTransactionCount() {
        return allTransactions.size();
    }

    /**
     * Prints transaction history for an account.
     */
    public void printAccountStatement(String accountId) {
        Account account = bankingService.findAccount(accountId);
        List<Transaction> history = account.getTransactionHistory();

        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.printf("║  📄 ACCOUNT STATEMENT: %s%n", accountId);
        System.out.printf("║  Holder: %s | Type: %s%n", account.getAccountHolder(), account.getAccountType());
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");

        if (history.isEmpty()) {
            System.out.println("║  No transactions found.");
        } else {
            System.out.printf("║  %-12s | %-12s | %12s | %12s%n",
                    "Transaction", "Type", "Amount", "Balance After");
            System.out.println("║  ─────────────┼──────────────┼──────────────┼──────────────");
            for (Transaction t : history) {
                System.out.printf("║  %-12s | %-12s | ₹%10.2f | ₹%10.2f%n",
                        t.getTransactionId(), t.getType(), t.getAmount(), t.getBalanceAfter());
            }
        }

        System.out.printf("║  Current Balance: ₹%.2f%n", account.getBalance());
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");
    }
}
