package com.banking.strategy;

import java.io.Serializable;

/**
 * Strategy interface for calculating interest.
 * Implements the Strategy design pattern to allow different interest
 * calculation algorithms.
 */
public interface InterestStrategy extends Serializable {

    /**
     * Calculates interest based on principal, rate, and time periods.
     *
     * @param principal  the principal amount
     * @param annualRate the annual interest rate as a percentage
     * @param periods    the number of periods (typically years)
     * @return the calculated interest amount
     */
    double calculateInterest(double principal, double annualRate, int periods);

    /**
     * Returns the name of this interest strategy.
     */
    String getStrategyName();
}
