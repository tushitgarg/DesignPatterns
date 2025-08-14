package Concurrency.chapter6;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorLifecycle {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit some tasks
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                try {
                    System.out.println("Executing task " + taskId + " in thread " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                    System.out.println("Finished task " + taskId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // This is how you shut down the executor gracefully
        System.out.println("Calling shutdown()...");
        executor.shutdown(); // Stops accepting new tasks

        // You can't submit tasks after shutdown
        // executor.execute(() -> System.out.println("This will be rejected"));

        System.out.println("Calling awaitTermination()...");
        // Wait for up to 10 seconds for existing tasks to complete
        if (executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("All tasks finished, executor terminated.");
        } else {
            System.out.println("Timeout elapsed. Forcing shutdown...");
            executor.shutdownNow();
        }
    }
}
