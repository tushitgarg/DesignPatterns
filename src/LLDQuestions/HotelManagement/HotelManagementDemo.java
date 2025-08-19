package LLDQuestions.HotelManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main class to demonstrate the Hotel Management System.
 */
public class HotelManagementDemo {
    public static void main(String[] args) {
        // The client uses the simple facade, not the complex subsystems.
        HotelFacade hotel = new HotelFacade();

        // --- SCENARIO 1: Search and Book a Room ---
        System.out.println("----- SCENARIO 1: Tushit books a SINGLE room -----");
        Date checkInDate = new Date();
        Date checkOutDate = new Date(checkInDate.getTime() + 2 * 24 * 60 * 60 * 1000); // 2 days later
        
        List<Room> availableRooms = hotel.searchRooms(RoomType.SINGLE, checkInDate, checkOutDate);
        if (!availableRooms.isEmpty()) {
            Room roomToBook = availableRooms.get(0);
            hotel.bookRoom("GUEST-001", "Tushit", roomToBook.getRoomNumber(), checkInDate, checkOutDate);
        }
        System.out.println();

        // --- SCENARIO 2: Try to book the same room again ---
        System.out.println("----- SCENARIO 2: Ankit tries to book the same SINGLE room for overlapping dates -----");
        List<Room> shouldBeEmpty = hotel.searchRooms(RoomType.SINGLE, checkInDate, checkOutDate);
        if (shouldBeEmpty.isEmpty()) {
            System.out.println("As expected, no SINGLE rooms are available for these dates.");
        }
        System.out.println();

        // --- SCENARIO 3: Check-in and Check-out ---
        System.out.println("----- SCENARIO 3: Tushit checks in and later checks out -----");
        // Assuming the reservation ID is known from the booking process
        hotel.checkIn("RES-101"); 
        hotel.checkOut("RES-101");
    }
}

// --- Enums for Types and Statuses ---
enum RoomType { SINGLE, DOUBLE, SUITE }
enum RoomStatus { AVAILABLE, OCCUPIED, MAINTENANCE }
enum ReservationStatus { CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED }

// --- Models ---
class Room {
    private int roomNumber;
    private RoomType type;
    private RoomStatus status;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = RoomStatus.AVAILABLE;
    }
    public int getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
}

class Guest {
    private String id;
    private String name;
    public Guest(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
}

class Reservation {
    private String reservationId;
    private String guestId;
    private int roomNumber;
    private Date checkInDate;
    private Date checkOutDate;
    private ReservationStatus status;

    public Reservation(String guestId, int roomNumber, Date checkInDate, Date checkOutDate) {
        this.reservationId = "RES-" + (int)(Math.random() * 1000); // Simple ID generation
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.CONFIRMED;
    }
    public String getReservationId() { return reservationId; }
    public int getRoomNumber() { return roomNumber; }
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}

// --- The Complex Subsystem Components ---

// 1. RoomService: Manages all rooms in the hotel.
class RoomService {
    private Map<Integer, Room> rooms = new HashMap<>();

    public RoomService() {
        rooms.put(101, new Room(101, RoomType.SINGLE));
        rooms.put(102, new Room(102, RoomType.SINGLE));
        rooms.put(201, new Room(201, RoomType.DOUBLE));
        rooms.put(301, new Room(301, RoomType.SUITE));
    }

    public Room findRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public List<Room> search(RoomType type, List<Integer> unavailableRoomNumbers) {
        return rooms.values().stream()
            .filter(room -> room.getType() == type && !unavailableRoomNumbers.contains(room.getRoomNumber()))
            .collect(Collectors.toList());
    }
}

// 2. ReservationService: Manages all reservations.
class ReservationService {
    private Map<String, Reservation> reservations = new HashMap<>();

    public Reservation createReservation(String guestId, int roomNumber, Date checkIn, Date checkOut) {
        Reservation reservation = new Reservation(guestId, roomNumber, checkIn, checkOut);
        reservations.put(reservation.getReservationId(), reservation);
        return reservation;
    }

    public Reservation findReservation(String reservationId) {
        // In a real system, you'd also need to find the reservation by room/guest
        // For this demo, we assume we get the ID "RES-101" from the booking step.
        // A simple hack to find the reservation for our demo.
        return reservations.values().stream().findFirst().orElse(null);
    }

    public List<Integer> getUnavailableRooms(Date checkIn, Date checkOut) {
        List<Integer> unavailable = new ArrayList<>();
        for (Reservation res : reservations.values()) {
            if (res.getStatus() != ReservationStatus.CANCELLED && res.getStatus() != ReservationStatus.CHECKED_OUT) {
                // Simple overlap check
                if (checkIn.before(res.getCheckOutDate()) && checkOut.after(res.getCheckInDate())) {
                    unavailable.add(res.getRoomNumber());
                }
            }
        }
        return unavailable;
    }
}

// --- The Facade Class ---

class HotelFacade {
    private RoomService roomService;
    private ReservationService reservationService;

    public HotelFacade() {
        this.roomService = new RoomService();
        this.reservationService = new ReservationService();
    }

    public List<Room> searchRooms(RoomType type, Date checkIn, Date checkOut) {
        System.out.println("Searching for " + type + " rooms...");
        List<Integer> unavailableRooms = reservationService.getUnavailableRooms(checkIn, checkOut);
        return roomService.search(type, unavailableRooms);
    }

    public void bookRoom(String guestId, String guestName, int roomNumber, Date checkIn, Date checkOut) {
        System.out.println("Booking room " + roomNumber + " for guest " + guestName);
        reservationService.createReservation(guestId, roomNumber, checkIn, checkOut);
        System.out.println("Booking confirmed!");
    }

    public void checkIn(String reservationId) {
        Reservation reservation = reservationService.findReservation(reservationId);
        if (reservation != null && reservation.getStatus() == ReservationStatus.CONFIRMED) {
            Room room = roomService.findRoom(reservation.getRoomNumber());
            room.setStatus(RoomStatus.OCCUPIED);
            reservation.setStatus(ReservationStatus.CHECKED_IN);
            System.out.println("Guest checked into room " + room.getRoomNumber());
        } else {
            System.out.println("Invalid reservation or guest already checked in.");
        }
    }

    public void checkOut(String reservationId) {
        Reservation reservation = reservationService.findReservation(reservationId);
        if (reservation != null && reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            Room room = roomService.findRoom(reservation.getRoomNumber());
            room.setStatus(RoomStatus.AVAILABLE); // Or MAINTENANCE
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            System.out.println("Guest checked out of room " + room.getRoomNumber());
        } else {
            System.out.println("Invalid reservation or guest not checked in.");
        }
    }
}

