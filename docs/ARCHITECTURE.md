# 🏦 Banking System Architecture

## Overview

This document describes the architecture and design patterns used in the Banking System Simulation.

---

## Class Diagram

```
                        ┌──────────────────┐
                        │   BankingApp     │ (Main Entry Point)
                        └────────┬─────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
    ┌─────────▼─────────┐ ┌─────▼──────────┐ ┌─────▼──────────┐
    │  BankingService    │ │ Transaction    │ │  LoanService   │
    │  (Singleton)       │ │ Service        │ │                │
    └─────────┬─────────┘ └──────┬─────────┘ └────────────────┘
              │                  │
    ┌─────────▼─────────┐ ┌─────▼──────────┐
    │  AccountFactory    │ │  Observers     │
    │  (Factory)         │ │  (Observer)    │
    └───────────────────┘ └────────────────┘
```

---

## Design Patterns

### 1. Factory Pattern — `AccountFactory`

**Purpose:** Encapsulates account creation logic, allowing the system to create different account types (Savings, Checking, Fixed Deposit) through a unified interface.

**How it works:**
- `AccountFactory.createAccount(type, id, holder, balance)` uses a switch expression to instantiate the appropriate subclass.
- Eliminates client code dependencies on concrete account classes.

---

### 2. Singleton Pattern — `BankingService`

**Purpose:** Ensures a single instance of the core banking service exists throughout the application lifecycle.

**How it works:**
- Double-checked locking with `volatile` keyword for thread safety.
- `BankingService.getInstance()` returns the single instance.
- `resetInstance()` provided for testing purposes.

---

### 3. Strategy Pattern — `InterestStrategy`

**Purpose:** Allows different interest calculation algorithms to be swapped at runtime.

**Implementations:**
| Strategy | Algorithm | Used By |
|----------|-----------|---------|
| `SavingsInterestStrategy` | Quarterly compound interest | Savings accounts |
| `FixedDepositInterestStrategy` | Annual compound interest | Fixed deposits |
| `LoanInterestStrategy` | EMI amortization | Loans |

---

### 4. Observer Pattern — `TransactionObserver`

**Purpose:** Decouples transaction processing from side effects like logging and fraud detection.

**Observers:**
- `TransactionLogger` — Records all transactions for auditing
- `FraudDetector` — Flags transactions above a configurable threshold

---

## OOP Principles

| Principle | Implementation |
|-----------|---------------|
| **Abstraction** | `Account` abstract class defines the contract |
| **Encapsulation** | Private fields with controlled access via getters/setters |
| **Inheritance** | `SavingsAccount`, `CheckingAccount`, `FixedDepositAccount` extend `Account` |
| **Polymorphism** | `deposit()`, `withdraw()`, `calculateInterest()` behave differently per account type |

---

## Exception Hierarchy

```
RuntimeException
  └── BankingException
        ├── InsufficientFundsException
        ├── InvalidAccountException
        └── LoanException
```

---

## Data Flow

```
User Input → BankingApp (CLI) → Service Layer → Model Layer → Persistence (File I/O)
                                      ↓
                              Observer Notifications
                              (Logging, Fraud Detection)
```

---

## Package Structure

| Package | Responsibility |
|---------|---------------|
| `com.banking` | Main application entry point |
| `com.banking.model` | Domain entities (Account, Customer, Transaction, Loan) |
| `com.banking.exception` | Custom exception hierarchy |
| `com.banking.service` | Business logic layer |
| `com.banking.factory` | Factory pattern implementation |
| `com.banking.strategy` | Strategy pattern for interest calculations |
| `com.banking.observer` | Observer pattern for transaction monitoring |
| `com.banking.util` | Utilities (persistence, validation, reports) |
