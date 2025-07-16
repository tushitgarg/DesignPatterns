package LLDQuestions.LRUCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Main class to demonstrate the LRU Cache.
 */
public class LRUCacheDemo {
    public static void main(String[] args) {
        System.out.println("--- Starting LRU Cache Demo ---");
        // Create a cache with a capacity of 3
        LRUCache cache = new LRUCache(3);

        cache.put(1, 10); // Cache: {1=10}
        cache.printCache();

        cache.put(2, 20); // Cache: {1=10, 2=20}
        cache.printCache();

        cache.put(3, 30); // Cache: {1=10, 2=20, 3=30}
        cache.printCache();

        System.out.println("\nGetting key 1: " + cache.get(1)); // Accessing 1 makes it most recently used
        cache.printCache(); // Order: 2, 3, 1

        System.out.println("\nPutting new item (4, 40). Cache is full. Should evict key 2.");
        cache.put(4, 40); // Evicts 2. Cache: {3=30, 1=10, 4=40}
        cache.printCache();

        System.out.println("\nGetting key 2 (should be evicted): " + cache.get(2)); // Returns -1
    }
}

/**
 * Node for the Doubly Linked List.
 * Contains key-value pair to allow quick deletion from the HashMap.
 */
class Node {
    int key;
    int value;
    Node prev;
    Node next;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

/**
 * The LRUCache class.
 * It uses a HashMap for O(1) lookups and a Doubly Linked List for O(1)
 * updates to the usage order.
 */
class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head; // Dummy head
    private final Node tail; // Dummy tail

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1, -1); // Dummy node
        this.tail = new Node(-1, -1); // Dummy node
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Retrieves an item from the cache. O(1) time complexity.
     */
    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1; // Not found
        }

        Node node = map.get(key);
        // Move the accessed node to the front (most recently used)
        moveToFront(node);
        return node.value;
    }

    /**
     * Inserts or updates an item in the cache. O(1) time complexity.
     */
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            // Key already exists, update its value and move to front
            Node node = map.get(key);
            node.value = value;
            moveToFront(node);
        } else {
            // Key does not exist, create a new node
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addFirst(newNode);

            // If capacity is exceeded, evict the least recently used item
            if (map.size() > capacity) {
                Node lruNode = removeLast();
                map.remove(lruNode.key);
            }
        }
    }

    // --- Doubly Linked List Helper Methods ---

    /**
     * Moves an existing node to the front of the list.
     */
    private void moveToFront(Node node) {
        removeNode(node);
        addFirst(node);
    }

    /**
     * Removes a node from its current position in the list.
     */
    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    /**
     * Adds a new node to the front of the list (right after the dummy head).
     */
    private void addFirst(Node node) {
        Node nextNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = nextNode;
        nextNode.prev = node;
    }

    /**
     * Removes the last node from the list (right before the dummy tail).
     * @return The removed node.
     */
    private Node removeLast() {
        Node lruNode = tail.prev;
        removeNode(lruNode);
        return lruNode;
    }

    /**
     * Helper method to print the current state of the cache for visualization.
     */
    public void printCache() {
        System.out.print("Cache content (LRU to MRU): ");
        Node current = head.next;
        while (current != tail) {
            System.out.print("{" + current.key + "=" + current.value + "} ");
            current = current.next;
        }
        System.out.println();
    }
}
