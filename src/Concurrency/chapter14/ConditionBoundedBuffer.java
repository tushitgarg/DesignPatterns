package Concurrency.chapter14;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionBoundedBuffer<T> {
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private final T[] buffer;
    private int tail, head, count;
    private final int capacity;

    public ConditionBoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (count == capacity) {
                notFull.await();
            }
            buffer[tail] = item;
            if(++tail == capacity) {
                tail = 0;
            }
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    private T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            T item = buffer[head];
            buffer[head] = null;
            if(++head == capacity) {
                head = 0;
            }
            count--;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
