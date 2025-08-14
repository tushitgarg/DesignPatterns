package Concurrency.chapter6;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableAndFutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Define a task that returns a value
        Callable<Integer> sumTask = () -> {
            System.out.println("Sum task is calculating...");
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
                Thread.sleep(10); // Simulate work
            }
            return sum;
        };

        // Submit the callable and get a future back
        Future<Integer> future = executor.submit(sumTask);
        System.out.println("Submitted the task. Doing other work...");

        // Do some other work while the task is running
        Thread.sleep(500);
        System.out.println("Other work finished. Waiting for the result...");

        // Block and get the result. This will wait if the task isn't done yet.
        Integer result = future.get(); // This is the blocking call
        System.out.println("The result is: " + result);

        executor.shutdown();
    }
}
