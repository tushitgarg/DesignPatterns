package Concurrency.chapter14;

public class BoundedBuffer<T> {
    private final T[] buffer;
    private int tail, head, count;
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    public synchronized void put(T item) throws InterruptedException {
        while(count == capacity) {
            wait();
        }
        buffer[tail] = item;
        if(++tail == capacity) {
            tail = 0;
        }
        count++;
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (count == 0) {
            wait();
        }

        T item = buffer[head];
        buffer[head] = null;
        if (++head == capacity) {
            head = 0;
        }
        count--;
        notifyAll();
        return item;
    }
}
