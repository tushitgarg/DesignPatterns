package Concurrency.chapter13;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockMap<K,V> {
    private final Map<K,V> map = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public V get(K key) {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "is reading.");
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {}
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "is writing.");
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args){
        ReadWriteLockMap<String, String> map = new ReadWriteLockMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Runnable writer = () -> map.put("key", "value");
        Runnable reader = () -> map.get("key");
        executor.submit(writer);
        executor.submit(reader);
        executor.submit(reader);
        executor.submit(reader);
        executor.submit(reader);
        executor.shutdown();
    }
}