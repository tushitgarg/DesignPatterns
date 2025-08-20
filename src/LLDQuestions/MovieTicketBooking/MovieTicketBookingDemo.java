package LLDQuestions.MovieTicketBooking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main demo class to simulate concurrent ticket booking.
 */
public class MovieTicketBookingDemo {
    public static void main(String[] args) throws InterruptedException {
        // Use the Singleton instance of the booking service
        BookingService bookingService = BookingService.getInstance();

        // --- Setup Data ---
        Movie movie = new Movie("M01", "Dune: Part Two");
        Cinema cinema = new Cinema("C01", "PVR Forum Mall, Bengaluru");
        Screen screen1 = new Screen("S01", cinema);
        Show show1 = new Show("SH01", movie, screen1, "2025-08-20T19:00:00");

        bookingService.addShow(show1);

        // --- Simulate two users trying to book the SAME seat concurrently ---
        System.out.println("--- Concurrent Booking Simulation ---");
        System.out.println("Tushit and Ankit are trying to book seat 'A1' for the same show simultaneously.");

        Seat seatToBook = screen1.getSeat("A1");

        // Create two booking tasks for the same seat
        Runnable task1 = () -> {
            System.out.println("Thread 1 (Tushit): Attempting to book seat A1...");
            bookingService.createBooking("USER-Tushit", show1, List.of(seatToBook));
        };

        Runnable task2 = () -> {
            System.out.println("Thread 2 (Ankit): Attempting to book seat A1...");
            bookingService.createBooking("USER-Ankit", show1, List.of(seatToBook));
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("\n--- Final State of Seat A1 ---");
        System.out.println("Seat A1 is booked: " + seatToBook.isBooked());
        System.out.println("Booking details: " + bookingService.getBookingByShow(show1).get(0));
    }
}

// --- Models ---
class Movie {
    String id;
    String title;
    public Movie(String id, String title) { this.id = id; this.title = title; }
    public String getTitle() { return title; }
}

class Cinema {
    String id;
    String name;
    public Cinema(String id, String name) { this.id = id; this.name = name; }
}

class Screen {
    String id;
    Cinema cinema;
    Map<String, Seat> seats = new HashMap<>();

    public Screen(String id, Cinema cinema) {
        this.id = id;
        this.cinema = cinema;
        // Create a simple 5x5 grid of seats
        for (char row = 'A'; row <= 'E'; row++) {
            for (int i = 1; i <= 5; i++) {
                String seatId = "" + row + i;
                seats.put(seatId, new Seat(seatId, row, i));
            }
        }
    }
    public Seat getSeat(String seatId) { return seats.get(seatId); }
}

class Seat {
    String id;
    char row;
    int number;
    private boolean isBooked;
    private final Lock lock = new ReentrantLock(); // Each seat has its own lock

    public Seat(String id, char row, int number) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.isBooked = false;
    }

    public boolean isBooked() { return isBooked; }
    public void book() { this.isBooked = true; }
    public void cancel() { this.isBooked = false; }
    public Lock getLock() { return lock; }
    @Override public String toString() { return id; }
}

class Show {
    String id;
    Movie movie;
    Screen screen;
    String showTime; // Using String for simplicity

    public Show(String id, Movie movie, Screen screen, String showTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.showTime = showTime;
    }
    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
}

class Booking {
    String id;
    String userId;
    Show show;
    List<Seat> bookedSeats;

    public Booking(String id, String userId, Show show, List<Seat> bookedSeats) {
        this.id = id;
        this.userId = userId;
        this.show = show;
        this.bookedSeats = bookedSeats;
    }
    @Override public String toString() {
        return "Booking ID: " + id + ", User: " + userId + ", Movie: " + show.getMovie().getTitle() + ", Seats: " + bookedSeats;
    }
}

// --- The Main Service (Singleton) ---
class BookingService {
    // The single, volatile instance for the Singleton pattern
    private static volatile BookingService instance;

    private final Map<String, Show> shows;
    private final Map<String, List<Booking>> bookings; // Map of showId to list of bookings

    // Private constructor to prevent direct instantiation
    private BookingService() {
        this.shows = new HashMap<>();
        this.bookings = new HashMap<>();
    }

    // Thread-safe getInstance method with double-checked locking
    public static BookingService getInstance() {
        if (instance == null) {
            synchronized (BookingService.class) {
                if (instance == null) {
                    instance = new BookingService();
                }
            }
        }
        return instance;
    }

    public void addShow(Show show) {
        shows.put(show.getId(), show);
        bookings.put(show.getId(), new ArrayList<>());
    }

    public List<Booking> getBookingByShow(Show show) {
        return bookings.get(show.getId());
    }

    /**
     * Creates a booking for a user for a specific show and seats.
     * This method is the critical section that handles concurrency.
     */
    public void createBooking(String userId, Show show, List<Seat> seatsToBook) {
        // 1. Check if all requested seats are available first.
        for (Seat seat : seatsToBook) {
            if (seat.isBooked()) {
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + userId + "): Booking failed. Seat " + seat.id + " is already booked.");
                return;
            }
        }

        // 2. Lock all the seats to prevent another thread from booking them.
        // This is a simplified locking strategy. A real system might sort seat IDs
        // to acquire locks in a consistent order to prevent deadlocks.
        for (Seat seat : seatsToBook) {
            seat.getLock().lock();
        }

        try {
            // 3. Re-check availability AFTER acquiring the locks (critical step).
            // Another thread might have booked the seat between the first check and now.
            for (Seat seat : seatsToBook) {
                if (seat.isBooked()) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + userId + "): Booking failed after lock. Seat " + seat.id + " was booked by another user.");
                    // Release locks for all seats if any one is not available
                    for(Seat s : seatsToBook) s.getLock().unlock();
                    return;
                }
            }

            // 4. If all seats are still available, proceed with booking.
            for (Seat seat : seatsToBook) {
                seat.book();
            }

            String bookingId = "B-" + (int)(Math.random() * 1000);
            Booking newBooking = new Booking(bookingId, userId, show, seatsToBook);
            bookings.get(show.getId()).add(newBooking);
            System.out.println("Thread " + Thread.currentThread().getId() + " (" + userId + "): Booking SUCCESSFUL! ID: " + bookingId);

        } finally {
            // 5. ALWAYS release the locks in a finally block.
            for (Seat seat : seatsToBook) {
                seat.getLock().unlock();
            }
        }
    }
}

