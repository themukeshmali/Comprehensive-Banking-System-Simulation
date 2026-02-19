package com.banking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a banking transaction record.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST, LOAN_DISBURSEMENT, LOAN_PAYMENT, FEE
    }

    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String sourceAccountId;
    private final String targetAccountId;
    private final String description;
    private final double balanceAfter;

    public Transaction(TransactionType type, double amount, String sourceAccountId,
            String targetAccountId, String description, double balanceAfter) {
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }

    // --- Getters ---

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public String getDescription() {
        return description;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s | %-12s | %-10s | ₹%10.2f | %s",
                transactionId, type, sourceAccountId != null ? sourceAccountId : "N/A",
                amount, timestamp.format(FORMATTER)));
        if (targetAccountId != null && !targetAccountId.isEmpty()) {
            sb.append(String.format(" → %s", targetAccountId));
        }
        if (description != null && !description.isEmpty()) {
            sb.append(String.format(" | %s", description));
        }
        return sb.toString();
    }
}
