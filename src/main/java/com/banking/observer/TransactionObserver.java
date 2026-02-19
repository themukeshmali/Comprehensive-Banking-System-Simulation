package com.banking.observer;

import com.banking.model.Transaction;

/**
 * Observer interface for monitoring transactions.
 * Implements the Observer design pattern.
 */
public interface TransactionObserver {

    /**
     * Called when a transaction is processed.
     *
     * @param transaction the processed transaction
     */
    void onTransaction(Transaction transaction);

    /**
     * Returns the name of this observer.
     */
    String getObserverName();
}
