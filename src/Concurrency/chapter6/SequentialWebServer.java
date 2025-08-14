package Concurrency.chapter6;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SequentialWebServer {
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws IOException {
        System.out.println("Server is listening on port 8080...");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                // This call blocks until a client connects.
                try {
                    Socket connection = serverSocket.accept();
                    System.out.println("Accepted connection from " + connection.getInetAddress());
                    Runnable task = () -> {
                        handleRequest(connection);
                    };
                    
                    // Submit the task to the executor. Don't create a new thread!
                    EXECUTOR.execute(task);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleRequest(Socket connection) {
        try (Socket conn = connection;
             OutputStream os = conn.getOutputStream()) {
            // Simulate a long-running task
            Thread.sleep(5000); // 5 seconds

            String response = "HTTP/1.1 200 OK\r\n\r\nHello, World!";
            os.write(response.getBytes("UTF-8"));
            System.out.println("Handled request and closed connection.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
