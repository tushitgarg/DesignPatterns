package Concurrency.Chapter8;

import java.util.concurrent.*;

public class ThreadDeadlock {

    // A single-threaded executor is the easiest way to demonstrate this.
    // A fixed pool of any size could also deadlock if it becomes saturated.
    static ExecutorService executor = Executors.newSingleThreadExecutor();

    public static class RenderPageTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("RenderPageTask: Starting to render page...");

            // Submit sub-tasks for header and footer
            Future<String> headerFuture = executor.submit(() -> {
                System.out.println("  Sub-task: Loading header...");
                Thread.sleep(2000); // Simulate network I/O
                return "<h1>Header</h1>";
            });

            Future<String> footerFuture = executor.submit(() -> {
                System.out.println("  Sub-task: Loading footer...");
                Thread.sleep(2000); // Simulate network I/O
                return "<footer>Footer</footer>";
            });

            String pageBody = "<p>This is the body of the page.</p>";

            // DEADLOCK HAPPENS HERE!
            // The RenderPageTask is holding the only thread in the pool,
            // but it's waiting for the header and footer tasks, which are
            // in the queue and can't run until a thread is free.
            System.out.println("RenderPageTask: Waiting for header and footer...");
            String header = headerFuture.get();
            String footer = footerFuture.get();

            return header + pageBody + footer;
        }
    }

    public static void main(String[] args) {
        try {
            Future<String> pageFuture = executor.submit(new RenderPageTask());
            // We set a timeout because this will hang forever otherwise.
            String page = pageFuture.get(5, TimeUnit.SECONDS);
            System.out.println("Page rendered successfully: " + page);
        } catch (Exception e) {
            System.err.println("An exception occurred (likely a timeout):");
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }
}
