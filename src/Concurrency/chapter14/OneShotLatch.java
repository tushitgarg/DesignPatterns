package Concurrency.chapter14;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class OneShotLatch {
    private final Sync sync = new Sync();
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public void signal() {
        sync.releaseShared(1);
    }

    private class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected int tryAcquireShared(int ignored) {
            return (getState() == 1) ? 1 : -1;

        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            setState(1);
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OneShotLatch latch = new OneShotLatch();

        // Start 10 threads that all wait on the latch
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " waiting...");
                try {
                    latch.await();
                    System.out.println(Thread.currentThread().getName() + " proceeded!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        Thread.sleep(2000); // Wait for a bit
        System.out.println("MAIN THREAD: Latch is being signalled!");
        latch.signal(); // Open the gate
    }
}
