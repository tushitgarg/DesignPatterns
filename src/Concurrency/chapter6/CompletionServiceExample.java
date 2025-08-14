package Concurrency.chapter6;

import java.util.concurrent.*;

public class CompletionServiceExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        // Submit tasks that take different amounts of time
        completionService.submit(() -> {
            Thread.sleep(3000);
            return "Task 1 (3s) Finished";
        });
        completionService.submit(() -> {
            Thread.sleep(1000);
            return "Task 2 (1s) Finished";
        });
        completionService.submit(() -> {
            Thread.sleep(2000);
            return "Task 3 (2s) Finished";
        });

        System.out.println("Submitted all tasks. Waiting for the first one to finish...");

        // Retrieve results in the order they complete
        for (int i = 0; i < 3; i++) {
            Future<String> completedFuture = completionService.take(); // Blocks until a task is done
            String result = completedFuture.get();
            System.out.println("Got result: " + result);
        }

        executor.shutdown();
    }
}
