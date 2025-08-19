package LLDQuestions.DigitalWallet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class to demonstrate the Digital Wallet system.
 */
public class DigitalWalletDemo {
    public static void main(String[] args) {
        WalletService walletService = new WalletService();

        // 1. Create users and their wallets
        walletService.createWallet("Tushit", 1000.0);
        walletService.createWallet("Ankit", 500.0);
        walletService.createWallet("Deepak", 2000.0);

        System.out.println("--- Initial Balances ---");
        walletService.printAllBalances();

        // 2. Perform a successful transaction
        System.out.println("\n--- Tushit sends 200 to Ankit ---");
        walletService.transferMoney("Tushit", "Ankit", 200.0);
        walletService.printAllBalances();

        // 3. Perform a transaction that should fail (insufficient funds)
        System.out.println("\n--- Ankit tries to send 1000 to Deepak (should fail) ---");
        walletService.transferMoney("Ankit", "Deepak", 1000.0);
        walletService.printAllBalances();

        // 4. Perform another successful transaction
        System.out.println("\n--- Deepak sends 500 to Tushit ---");
        walletService.transferMoney("Deepak", "Tushit", 500.0);
        walletService.printAllBalances();

        // 5. View transaction history for Tushit
        System.out.println("\n--- Tushit's Transaction History ---");
        walletService.printTransactionHistory("Tushit");
    }
}

// --- Models ---

class User {
    private String name;
    private Wallet wallet;

    public User(String name, double initialBalance) {
        this.name = name;
        this.wallet = new Wallet(initialBalance);
    }
    public String getName() { return name; }
    public Wallet getWallet() { return wallet; }
}

class Wallet {
    private double balance;
    private List<Transaction> transactions;

    public Wallet(double initialBalance) {
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    // These methods are synchronized to ensure thread-safety on the balance
    public synchronized void deposit(double amount) {
        this.balance += amount;
    }

    public synchronized void withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {
            throw new IllegalArgumentException("Insufficient funds.");
        }
    }

    public synchronized void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}

enum TransactionType { DEBIT, CREDIT }

class Transaction {
    private double amount;
    private TransactionType type;
    private Date date;
    private String description;

    public Transaction(double amount, TransactionType type, String description) {
        this.amount = amount;
        this.type = type;
        this.date = new Date();
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s of %.2f. Description: %s", date, type, amount, description);
    }
}


// --- The Main Service Class ---

class WalletService {
    // Using ConcurrentHashMap for thread-safe access to the user map itself
    private final Map<String, User> userAccounts = new ConcurrentHashMap<>();
    // A lock object for controlling access during transfers to prevent deadlocks
    private final Object transferLock = new Object();

    public void createWallet(String name, double initialBalance) {
        if (userAccounts.containsKey(name)) {
            System.out.println("User " + name + " already exists.");
            return;
        }
        userAccounts.put(name, new User(name, initialBalance));
        System.out.println("Wallet for " + name + " created with balance: " + initialBalance);
    }

    /**
     * Transfers money from one user to another atomically.
     */
    public void transferMoney(String fromUserName, String toUserName, double amount) {
        User fromUser = userAccounts.get(fromUserName);
        User toUser = userAccounts.get(toUserName);

        if (fromUser == null || toUser == null) {
            System.out.println("Error: One or both users not found.");
            return;
        }
        
        if (fromUser.getWallet().getBalance() < amount) {
            System.out.println("Error: Insufficient funds for " + fromUserName);
            return;
        }

        // To prevent deadlocks (e.g., Thread1 transfers A->B, Thread2 transfers B->A),
        // we acquire locks in a consistent order. Here we use a single global lock
        // for simplicity, but in a real high-performance system, you might lock
        // user accounts in a fixed order (e.g., by their ID).
        synchronized (transferLock) {
            try {
                // 1. Debit from sender
                fromUser.getWallet().withdraw(amount);
                fromUser.getWallet().addTransaction(new Transaction(amount, TransactionType.DEBIT, "Sent to " + toUserName));

                // 2. Credit to receiver
                toUser.getWallet().deposit(amount);
                toUser.getWallet().addTransaction(new Transaction(amount, TransactionType.CREDIT, "Received from " + fromUserName));
                
                System.out.println("Success: Transferred " + amount + " from " + fromUserName + " to " + toUserName);

            } catch (IllegalArgumentException e) {
                // This catch block is crucial. If withdraw fails, the transaction is rolled back
                // because the deposit code is never reached.
                System.out.println("Transaction failed: " + e.getMessage());
            }
        }
    }

    public void printTransactionHistory(String userName) {
        User user = userAccounts.get(userName);
        if (user != null) {
            user.getWallet().getTransactions().forEach(System.out::println);
        } else {
            System.out.println("User not found.");
        }
    }

    public void printAllBalances() {
        System.out.println("  Current Balances:");
        for (User user : userAccounts.values()) {
            System.out.println("    - " + user.getName() + ": " + String.format("%.2f", user.getWallet().getBalance()));
        }
    }
}
