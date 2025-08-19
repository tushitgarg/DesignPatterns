package LLDQuestions.DigitalWallet.concurrent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class to demonstrate a concurrent Digital Wallet system.
 */
public class DigitalWalletConcurrentDemo {
    public static void main(String[] args) throws InterruptedException {
        // Use a fixed thread pool to process transfers concurrently
        ExecutorService executor = Executors.newFixedThreadPool(10);
        WalletService walletService = new WalletService(executor);

        // 1. Create users
        walletService.createWallet("Tushit", 1000.0);
        walletService.createWallet("Ankit", 1000.0);
        walletService.createWallet("Deepak", 1000.0);

        System.out.println("--- Initial Balances ---");
        walletService.printAllBalances();

        // 2. Submit a high volume of concurrent, conflicting transfers
        System.out.println("\n--- Submitting 100s of concurrent transfers (A->B, B->A, A->C, etc.) ---");
        for (int i = 0; i < 100; i++) {
            walletService.transferMoney("Tushit", "Ankit", 1.0);
            walletService.transferMoney("Ankit", "Tushit", 1.0); // Conflicting transfer
            walletService.transferMoney("Tushit", "Deepak", 1.0);
            walletService.transferMoney("Deepak", "Ankit", 1.0);
        }

        // 3. Shut down the executor and wait for tasks to complete
        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);

        if (finished) {
            System.out.println("\n--- All transfers processed. Final Balances: ---");
            walletService.printAllBalances();
            
            System.out.println("\n--- Tushit's Transaction History ---");
            walletService.printTransactionHistory("Tushit");
        } else {
            System.out.println("Error: Transfers timed out.");
        }
    }
}

// --- Models ---

class User {
    private final String name; // Use name as a unique, comparable ID
    private final Wallet wallet;

    public User(String name, double initialBalance) {
        this.name = name;
        this.wallet = new Wallet(initialBalance);
    }
    public String getName() { return name; }
    public Wallet getWallet() { return wallet; }
}

class Wallet {
    private double balance;
    private final List<Transaction> transactions;
    private final Lock lock = new ReentrantLock(); // Each wallet has its own lock

    public Wallet(double initialBalance) {
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }
    
    public Lock getLock() { return lock; }
    public double getBalance() { return balance; }
    public void deposit(double amount) { this.balance += amount; }
    public void withdraw(double amount) { this.balance -= amount; }
    public void addTransaction(Transaction t) { this.transactions.add(t); }
    public List<Transaction> getTransactions() { return transactions; }
}

enum TransactionType { DEBIT, CREDIT }

class Transaction {
    private final double amount;
    private final TransactionType type;
    private final Date date;
    private final String description;

    public Transaction(double amount, TransactionType type, String description) {
        this.amount = amount;
        this.type = type;
        this.date = new Date();
        this.description = description;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s of %.2f. Desc: %s", date, type, amount, description);
    }
}

// --- The Main Service Class ---

class WalletService {
    private final Map<String, User> userAccounts = new ConcurrentHashMap<>();
    private final ExecutorService executor;

    public WalletService(ExecutorService executor) {
        this.executor = executor;
    }

    public void createWallet(String name, double initialBalance) {
        userAccounts.put(name, new User(name, initialBalance));
    }

    /**
     * Submits a transfer task to the executor service for asynchronous processing.
     */
    public void transferMoney(String fromUserName, String toUserName, double amount) {
        User fromUser = userAccounts.get(fromUserName);
        User toUser = userAccounts.get(toUserName);

        if (fromUser == null || toUser == null || amount <= 0) {
            System.out.println("Invalid transfer request.");
            return;
        }
        // Create a task for the transfer and submit it to the thread pool
        executor.submit(new TransferTask(fromUser, toUser, amount));
    }

    public void printTransactionHistory(String userName) {
        User user = userAccounts.get(userName);
        if (user != null) {
            user.getWallet().getTransactions().forEach(System.out::println);
        }
    }

    public void printAllBalances() {
        System.out.println("  Current Balances:");
        userAccounts.values().stream()
            .sorted((u1, u2) -> u1.getName().compareTo(u2.getName()))
            .forEach(user -> System.out.println(
                "    - " + user.getName() + ": " + String.format("%.2f", user.getWallet().getBalance())
            ));
    }
}

// --- The Concurrent Task ---

class TransferTask implements Runnable {
    private final User fromUser;
    private final User toUser;
    private final double amount;

    public TransferTask(User fromUser, User toUser, double amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    @Override
    public void run() {
        // To prevent deadlock, we establish a consistent lock acquisition order.
        User first = fromUser.getName().compareTo(toUser.getName()) < 0 ? fromUser : toUser;
        User second = first == fromUser ? toUser : fromUser;

        // Acquire locks in the defined order
        first.getWallet().getLock().lock();
        second.getWallet().getLock().lock();

        try {
            // Perform the check and act atomically
            if (fromUser.getWallet().getBalance() >= amount) {
                fromUser.getWallet().withdraw(amount);
                toUser.getWallet().deposit(amount);

                // Add transaction records
                String fromDesc = "Sent to " + toUser.getName();
                fromUser.getWallet().addTransaction(new Transaction(amount, TransactionType.DEBIT, fromDesc));
                String toDesc = "Received from " + fromUser.getName();
                toUser.getWallet().addTransaction(new Transaction(amount, TransactionType.CREDIT, toDesc));
                
                // System.out.println("Success: " + fromUser.getName() + " -> " + toUser.getName() + ": " + amount);
            } else {
                // System.out.println("Failed (insufficient funds): " + fromUser.getName() + " -> " + toUser.getName());
            }
        } finally {
            // ALWAYS release locks in the reverse order of acquisition in a finally block
            second.getWallet().getLock().unlock();
            first.getWallet().getLock().unlock();
        }
    }
}

