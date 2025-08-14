package Concurrency.Chapter8;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class TimingThreadPool extends ThreadPoolExecutor {
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final Logger log = Logger.getLogger("TimingThreadPool");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool() {
        super(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.info(String.format("Thread %s: start %s", t.getName(), r.hashCode()));
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            Long start = startTime.get();
            long taskTime = (start != null) ? (endTime - start) : 0L;
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.info(String.format("Thread %s: end %s, time=%dns", Thread.currentThread().getName(), r.hashCode(), taskTime));
        } finally {
            startTime.remove();
            super.afterExecute(r, t);
        }
    }

    @Override
    protected void terminated() {
        try {
            long n = numTasks.get();
            if (n == 0) {
                log.info("Terminated: no tasks executed");
            } else {
                log.info(String.format("Terminated: avg time=%dns", totalTime.get() / n));
            }
        } finally {
            super.terminated();
        }
    }

    public static void main(String[] args) {
        TimingThreadPool executor = new TimingThreadPool();
        executor.execute(() -> {
            // Simulate work
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.shutdown();
    }
}