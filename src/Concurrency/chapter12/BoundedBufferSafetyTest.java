package Concurrency.chapter12;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BoundedBufferSafetyTest {
    private final BlockingQueue<Integer> queue;
    private final int nTrials;
    private final int nPairs;
    private final CyclicBarrier barrier;
    // Using AtomicIntegers for thread-safe summing
    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);

    public BoundedBufferSafetyTest(int capacity, int nPairs, int nTrials) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.nPairs = nPairs;
        this.nTrials = nTrials;
        this.barrier = new CyclicBarrier(nPairs * 2 + 1); // +1 for the main thread
    }

    void test() {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < nPairs; i++) {
                executor.execute(new Producer());
                executor.execute(new Consumer());
            }
            barrier.await(); // Wait for all threads to be ready
            barrier.await(); // Wait for all threads to finish
            
            // The sum of items put should equal the sum of items taken
            if (putSum.get() != takeSum.get()) {
                System.err.println("Test FAILED: putSum=" + putSum.get() + ", takeSum=" + takeSum.get());
            } else {
                System.out.println("Test PASSED!");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    class Producer implements Runnable {
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = nTrials; i > 0; --i) {
                    queue.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.addAndGet(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += queue.take();
                }
                takeSum.addAndGet(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    // A simple pseudo-random number generator
    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    public static void main(String[] args) {
        // Parameters: capacity, number of producer-consumer pairs, trials per thread
        new BoundedBufferSafetyTest(10, 10, 100000).test();
    }
}