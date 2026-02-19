package com.banking.observer;

import com.banking.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer that detects potentially fraudulent transactions.
 * Flags transactions exceeding a configurable threshold.
 */
public class FraudDetector implements TransactionObserver {
    private static final double DEFAULT_THRESHOLD = 100000.0; // ₹1,00,000

    private double alertThreshold;
    private final List<Transaction> flaggedTransactions;

    public FraudDetector() {
        this.alertThreshold = DEFAULT_THRESHOLD;
        this.flaggedTransactions = new ArrayList<>();
    }

    public FraudDetector(double alertThreshold) {
        this.alertThreshold = alertThreshold;
        this.flaggedTransactions = new ArrayList<>();
    }

    @Override
    public void onTransaction(Transaction transaction) {
        if (transaction.getAmount() >= alertThreshold) {
            flaggedTransactions.add(transaction);
            System.out.printf("🚨 FRAUD ALERT: Large transaction detected! %s | Amount: ₹%.2f | Type: %s%n",
                    transaction.getTransactionId(), transaction.getAmount(), transaction.getType());
        }
    }

    @Override
    public String getObserverName() {
        return "Fraud Detector";
    }

    /**
     * Returns all flagged transactions.
     */
    public List<Transaction> getFlaggedTransactions() {
        return Collections.unmodifiableList(flaggedTransactions);
    }

    /**
     * Returns the number of flagged transactions.
     */
    public int getFlaggedCount() {
        return flaggedTransactions.size();
    }

    /**
     * Prints flagged transactions report.
     */
    public void printFlaggedReport() {
        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("           🚨 FRAUD DETECTION REPORT");
        System.out.printf("           Threshold: ₹%.2f%n", alertThreshold);
        System.out.println("═══════════════════════════════════════════════");
        if (flaggedTransactions.isEmpty()) {
            System.out.println("  ✅ No suspicious transactions detected.");
        } else {
            System.out.printf("  ⚠ %d suspicious transaction(s) flagged:%n", flaggedTransactions.size());
            flaggedTransactions.forEach(t -> System.out.printf("    - %s | ₹%.2f | %s%n",
                    t.getTransactionId(), t.getAmount(), t.getType()));
        }
        System.out.println("═══════════════════════════════════════════════\n");
    }

    // --- Getters & Setters ---

    public double getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(double alertThreshold) {
        this.alertThreshold = alertThreshold;
    }
}
