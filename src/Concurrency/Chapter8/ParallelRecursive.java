package Concurrency.Chapter8;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// A simple Node class for our tree
class Node<T> {
    private final T value;
    private final List<Node<T>> children = new ArrayList<>();

    Node(T value) { this.value = value; }
    void addChild(Node<T> child) { children.add(child); }
    T compute() {
        // Simulate some work
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return value;
    }
    List<Node<T>> getChildren() { return children; }
}

public class ParallelRecursive {

    // The parallel traversal method
    public <T> void parallelRecursive(final Executor exec, List<Node<T>> nodes, final Collection<T> results) {
        for (final Node<T> n : nodes) {
            exec.execute(() -> results.add(n.compute()));
            parallelRecursive(exec, n.getChildren(), results);
        }
    }

    // A method to start the process and wait for all results
    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedQueue<>();
        parallelRecursive(exec, nodes, resultQueue);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }

    public static void main(String[] args) throws InterruptedException {
        // Create a simple tree structure
        Node<Integer> root = new Node<>(1);
        Node<Integer> child1 = new Node<>(2);
        Node<Integer> child2 = new Node<>(3);
        Node<Integer> grandChild1 = new Node<>(4);
        child1.addChild(grandChild1);
        root.addChild(child1);
        root.addChild(child2);

        List<Node<Integer>> initialNodes = new ArrayList<>();
        initialNodes.add(root);

        ParallelRecursive pr = new ParallelRecursive();
        long startTime = System.nanoTime();
        Collection<Integer> results = pr.getParallelResults(initialNodes);
        long endTime = System.nanoTime();

        System.out.println("Results: " + results);
        System.out.println("Time taken: " + (endTime - startTime) / 1_000_000 + " ms");
    }
}
