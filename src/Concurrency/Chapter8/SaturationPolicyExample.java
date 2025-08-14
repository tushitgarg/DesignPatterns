package Concurrency.Chapter8;

import java.util.concurrent.*;

public class SaturationPolicyExample {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, // corePoolSize
                2, // maximumPoolSize
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(2) // workQueue with capacity 2
        );

        // Use the CallerRunsPolicy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        System.out.println("Submitting tasks...");

        // Submit more tasks than the pool and queue can handle (2 threads + 2 queue = 4 capacity)
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Executing task " + taskId + " in thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            System.out.println("  Submitted task " + taskId);
        }

        System.out.println("All tasks submitted.");
        executor.shutdown();
    }
}
