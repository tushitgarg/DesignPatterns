package Concurrency.chapter13;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {
    private final Lock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock(); // Acquire the lock
        try {
            count++;
            System.out.println("Count is now: " + count + " by " + Thread.currentThread().getName());
        } finally {
            lock.unlock(); // ALWAYS release the lock in a finally block
        }
    }

    public static void main(String[] args) {
        ReentrantLockExample example = new ReentrantLockExample();
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                example.increment();
            }
        };
        new Thread(task).start();
        new Thread(task).start();
    }
}