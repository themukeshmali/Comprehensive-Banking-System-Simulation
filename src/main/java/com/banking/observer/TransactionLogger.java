package com.banking.observer;

import com.banking.model.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer that logs all transactions for audit purposes.
 */
public class TransactionLogger implements TransactionObserver {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final List<String> auditLog;

    public TransactionLogger() {
        this.auditLog = new ArrayList<>();
    }

    @Override
    public void onTransaction(Transaction transaction) {
        String logEntry = String.format("[%s] %s | %s | Amount: ₹%.2f | Account: %s",
                transaction.getTimestamp().format(FORMATTER),
                transaction.getTransactionId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getSourceAccountId() != null ? transaction.getSourceAccountId() : "N/A");

        if (transaction.getTargetAccountId() != null) {
            logEntry += " → " + transaction.getTargetAccountId();
        }

        auditLog.add(logEntry);
        System.out.println("📝 LOG: " + logEntry);
    }

    @Override
    public String getObserverName() {
        return "Transaction Logger";
    }

    /**
     * Returns all logged entries.
     */
    public List<String> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    /**
     * Prints full audit log.
     */
    public void printAuditLog() {
        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("              📋 AUDIT LOG");
        System.out.println("═══════════════════════════════════════════════");
        if (auditLog.isEmpty()) {
            System.out.println("  No transactions logged.");
        } else {
            auditLog.forEach(entry -> System.out.println("  " + entry));
        }
        System.out.println("═══════════════════════════════════════════════\n");
    }
}
