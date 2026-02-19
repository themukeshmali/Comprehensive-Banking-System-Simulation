# 🏦 Banking System Simulation

A comprehensive banking system simulation built with **Java 17** and **Maven**, featuring account management, transactions, loans, and interest calculations. The project demonstrates advanced Java concepts including OOP, design patterns, custom exceptions, generics, and file I/O.

---

## 📋 Features

### Account Management
- **Savings Account** — Minimum balance enforcement, compound interest
- **Checking Account** — Overdraft protection with configurable limits
- **Fixed Deposit Account** — Maturity-based deposits with early withdrawal penalties

### Transaction Processing
- Deposits, withdrawals, and inter-account transfers
- Full transaction history with timestamps
- Observer-based transaction logging and fraud detection

### Loan Management
- Multiple loan types: Personal, Home, Auto, Education
- EMI (Equated Monthly Installment) calculation
- Loan eligibility checks and payment processing

### Interest Calculations
- Strategy pattern for flexible interest computation
- Compound interest for savings and fixed deposits
- Amortization-based loan interest

---

## 🏗️ Design Patterns Used

| Pattern | Usage |
|---------|-------|
| **Factory** | `AccountFactory` creates account instances by type |
| **Singleton** | `BankingService` ensures a single service instance |
| **Strategy** | `InterestStrategy` allows swappable interest algorithms |
| **Observer** | `TransactionObserver` for logging and fraud detection |

---

## 📁 Project Structure

```
Mark-ii/
├── pom.xml                          # Maven build config
├── README.md                        # This file
├── LICENSE                          # MIT License
├── .gitignore                       # Git ignore rules
├── data/                            # Sample data files
├── docs/                            # Architecture documentation
│   └── ARCHITECTURE.md
└── src/
    ├── main/java/com/banking/
    │   ├── BankingApp.java          # Main CLI application
    │   ├── model/                   # Domain models
    │   ├── exception/               # Custom exceptions
    │   ├── service/                 # Business logic
    │   ├── factory/                 # Factory pattern
    │   ├── strategy/                # Strategy pattern
    │   ├── observer/                # Observer pattern
    │   └── util/                    # Utilities
    └── test/java/com/banking/       # JUnit 5 tests
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Build & Run

```bash
# Clone the repository
git clone <repository-url>
cd Mark-ii

# Compile the project
mvn clean compile

# Run tests
mvn test

# Package and run
mvn package
java -jar target/banking-system-1.0-SNAPSHOT.jar
```

### Run directly with Maven
```bash
mvn exec:java -Dexec.mainClass="com.banking.BankingApp"
```

---

## 🧪 Testing

The project includes comprehensive JUnit 5 tests:

```bash
mvn test
```

**Test Coverage:**
- Model tests (Account types, Customer)
- Service tests (Banking, Transaction, Loan services)
- Strategy tests (Interest calculation algorithms)

---

## 📖 Documentation

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed architecture and design documentation.

---

## 🛠️ Technologies

- **Language:** Java 17
- **Build Tool:** Maven
- **Testing:** JUnit 5
- **Persistence:** Java Serialization (File I/O)

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
