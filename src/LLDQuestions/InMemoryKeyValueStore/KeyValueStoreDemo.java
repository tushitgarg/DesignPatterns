package LLDQuestions.InMemoryKeyValueStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Main class to demonstrate the In-Memory Key-Value Store with transactions.
 */
public class KeyValueStoreDemo {
    public static void main(String[] args) {
        KeyValueStore store = new KeyValueStore();

        // --- SCENARIO 1: Basic Operations ---
        System.out.println("----- SCENARIO 1: Basic PUT/GET/DELETE -----");
        store.put("a", "1");
        store.put("b", "2");
        System.out.println("Get 'a': " + store.get("a")); // Expected: 1
        store.delete("b");
        System.out.println("Get 'b' after delete: " + store.get("b")); // Expected: null
        System.out.println();

        // --- SCENARIO 2: Transaction with COMMIT ---
        System.out.println("----- SCENARIO 2: Transaction with COMMIT -----");
        store.put("x", "100"); // Pre-existing value
        store.begin();
        store.put("x", "200"); // Update 'x' inside transaction
        store.put("y", "300"); // Create 'y' inside transaction
        System.out.println("Get 'x' inside transaction: " + store.get("x")); // Expected: 200
        System.out.println("Get 'y' inside transaction: " + store.get("y")); // Expected: 300
        store.commit();
        System.out.println("Get 'x' after commit: " + store.get("x")); // Expected: 200
        System.out.println("Get 'y' after commit: " + store.get("y")); // Expected: 300
        System.out.println();

        // --- SCENARIO 3: Transaction with ROLLBACK ---
        System.out.println("----- SCENARIO 3: Transaction with ROLLBACK -----");
        store.put("p", "50"); // Pre-existing value
        store.begin();
        store.put("p", "60"); // Update 'p'
        store.put("q", "70"); // Create 'q'
        System.out.println("Get 'p' inside transaction: " + store.get("p")); // Expected: 60
        store.rollback();
        System.out.println("Get 'p' after rollback: " + store.get("p")); // Expected: 50
        System.out.println("Get 'q' after rollback: " + store.get("q")); // Expected: null
    }
}

// The core data store
class KeyValueStore {
    private Map<String, String> mainStore;
    private Transaction currentTransaction;

    public KeyValueStore() {
        this.mainStore = new HashMap<>();
        this.currentTransaction = null;
    }

    public void put(String key, String value) {
        if (isInTransaction()) {
            currentTransaction.put(key, value);
        } else {
            mainStore.put(key, value);
        }
    }

    public String get(String key) {
        if (isInTransaction()) {
            // First, check the transactional store.
            // Optional is used to handle keys marked for deletion.
            Optional<String> transactionalValue = currentTransaction.get(key);
            if (transactionalValue != null) {
                return transactionalValue.orElse(null);
            }
        }
        // If not in transaction or not found in transaction, check the main store.
        return mainStore.get(key);
    }

    public void delete(String key) {
        if (isInTransaction()) {
            currentTransaction.delete(key);
        } else {
            mainStore.remove(key);
        }
    }

    // --- Transactional Methods ---
    public void begin() {
        if (isInTransaction()) {
            throw new IllegalStateException("A transaction is already in progress.");
        }
        this.currentTransaction = new Transaction();
    }

    public void commit() {
        if (!isInTransaction()) {
            throw new IllegalStateException("No transaction to commit.");
        }
        currentTransaction.commit(mainStore);
        this.currentTransaction = null;
    }

    public void rollback() {
        if (!isInTransaction()) {
            throw new IllegalStateException("No transaction to rollback.");
        }
        this.currentTransaction = null; // Just discard the transaction
    }


    private boolean isInTransaction() {
        return currentTransaction != null;
    }
}

// Represents a single transaction, holding temporary changes.
class Transaction {
    // A temporary store for changes made within this transaction.
    // We use Optional<String> to differentiate between a key not being in the
    // transaction vs. a key being explicitly deleted (value = Optional.empty()).
    private Map<String, Optional<String>> tempStore;

    public Transaction() {
        this.tempStore = new HashMap<>();
    }

    public void put(String key, String value) {
        tempStore.put(key, Optional.of(value));
    }

    public Optional<String> get(String key) {
        return tempStore.get(key);
    }

    public void delete(String key) {
        tempStore.put(key, Optional.empty());
    }

    /**
     * Applies the changes from this transaction to the main data store.
     */
    public void commit(Map<String, String> mainStore) {
        for (Map.Entry<String, Optional<String>> entry : tempStore.entrySet()) {
            String key = entry.getKey();
            Optional<String> value = entry.getValue();

            if (value.isPresent()) {
                // This was a PUT operation
                mainStore.put(key, value.get());
            } else {
                // This was a DELETE operation
                mainStore.remove(key);
            }
        }
    }
}

