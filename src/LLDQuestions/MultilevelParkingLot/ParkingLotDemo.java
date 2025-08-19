import java.util.ArrayList;
import java.util.List;

/**
 * Main class to demonstrate the Parking Lot system.
 */
public class ParkingLotDemo {
    public static void main(String[] args) {
        // 1. Create the Parking Lot with a specific strategy
        ParkingLot parkingLot = new ParkingLot(2, 5); // 2 floors, 5 spots per floor
        System.out.println("Parking Lot created with " + parkingLot.getFloors().size() + " floors.");

        // 2. Create some vehicles
        Vehicle car1 = new Vehicle("CAR-001", VehicleType.CAR);
        Vehicle motorcycle1 = new Vehicle("MOTO-001", VehicleType.MOTORCYCLE);
        Vehicle car2 = new Vehicle("CAR-002", VehicleType.CAR);

        // 3. Park the vehicles
        System.out.println("\n--- Parking Vehicles ---");
        parkingLot.parkVehicle(car1);
        parkingLot.parkVehicle(motorcycle1);
        parkingLot.parkVehicle(car2);

        // 4. Try to park a vehicle when no spots are available for it
        Vehicle car3 = new Vehicle("CAR-003", VehicleType.CAR);
        parkingLot.parkVehicle(car3); // Will likely find a spot
        Vehicle car4 = new Vehicle("CAR-004", VehicleType.CAR);
        parkingLot.parkVehicle(car4); // Will likely find a spot
        Vehicle car5 = new Vehicle("CAR-005", VehicleType.CAR);
        parkingLot.parkVehicle(car5); // Will likely find a spot
        Vehicle car6 = new Vehicle("CAR-006", VehicleType.CAR);
        parkingLot.parkVehicle(car6); // Should fail as all car spots are taken

        // 5. Unpark a vehicle
        System.out.println("\n--- Unparking Vehicle ---");
        parkingLot.unparkVehicle(car1);

        // 6. Try parking the last car again
        System.out.println("\n--- Trying to Park Again ---");
        parkingLot.parkVehicle(car6); // Should now succeed
    }
}

// --- Enums for Types ---
enum VehicleType { MOTORCYCLE, CAR, TRUCK }
enum SpotType { MOTORCYCLE_SPOT, COMPACT_SPOT, LARGE_SPOT }

// --- Models ---
class Vehicle {
    private String licensePlate;
    private VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    public VehicleType getType() { return type; }
    public String getLicensePlate() { return licensePlate; }
}

class ParkingSpot {
    private int spotNumber;
    private SpotType type;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(int spotNumber, SpotType type) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.isOccupied = false;
    }

    public boolean isOccupied() { return isOccupied; }
    public SpotType getType() { return type; }
    public int getSpotNumber() { return spotNumber; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public boolean canFitVehicle(Vehicle vehicle) {
        if (isOccupied) return false;
        // A car can fit in a compact or large spot
        if (vehicle.getType() == VehicleType.CAR) {
            return this.type == SpotType.COMPACT_SPOT || this.type == SpotType.LARGE_SPOT;
        }
        // A motorcycle can fit in any spot type
        if (vehicle.getType() == VehicleType.MOTORCYCLE) {
            return true;
        }
        // A truck can only fit in a large spot
        if (vehicle.getType() == VehicleType.TRUCK) {
            return this.type == SpotType.LARGE_SPOT;
        }
        return false;
    }

    public void park(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    public void unpark() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
}

class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, int numSpots) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        // Simple logic to create a mix of spot types
        for (int i = 0; i < numSpots; i++) {
            if (i < 2) spots.add(new ParkingSpot(i, SpotType.MOTORCYCLE_SPOT));
            else if (i < 4) spots.add(new ParkingSpot(i, SpotType.COMPACT_SPOT));
            else spots.add(new ParkingSpot(i, SpotType.LARGE_SPOT));
        }
    }
    public List<ParkingSpot> getSpots() { return spots; }
}

class Ticket {
    private String vehicleLicensePlate;
    private int spotNumber;
    private int floorNumber;
    private long entryTime;

    public Ticket(Vehicle vehicle, ParkingSpot spot, int floorNumber) {
        this.vehicleLicensePlate = vehicle.getLicensePlate();
        this.spotNumber = spot.getSpotNumber();
        this.floorNumber = floorNumber;
        this.entryTime = System.currentTimeMillis();
    }
    // Getters...
}

// --- STRATEGY PATTERN for Parking Logic ---
interface ParkingStrategy {
    ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}

class DefaultParkingStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.canFitVehicle(vehicle)) {
                    return spot;
                }
            }
        }
        return null; // No spot found
    }
}

// --- The Main Context Class ---
class ParkingLot {
    private List<ParkingFloor> floors;
    private ParkingStrategy parkingStrategy;
    private List<Ticket> tickets;

    public ParkingLot(int numFloors, int numSpotsPerFloor) {
        this.floors = new ArrayList<>();
        for (int i = 0; i < numFloors; i++) {
            floors.add(new ParkingFloor(i, numSpotsPerFloor));
        }
        this.parkingStrategy = new DefaultParkingStrategy(); // Default strategy
        this.tickets = new ArrayList<>();
    }
    
    public List<ParkingFloor> getFloors() {
        return floors;
    }

    public void setParkingStrategy(ParkingStrategy parkingStrategy) {
        this.parkingStrategy = parkingStrategy;
    }

    public void parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = parkingStrategy.findSpot(floors, vehicle);
        if (spot != null) {
            spot.park(vehicle);
            // This is a simplified way to find the floor number
            int floorNumber = -1;
            for(int i=0; i<floors.size(); i++){
                if(floors.get(i).getSpots().contains(spot)){
                    floorNumber = i;
                    break;
                }
            }
            Ticket ticket = new Ticket(vehicle, spot, floorNumber);
            tickets.add(ticket);
            System.out.println("Vehicle " + vehicle.getLicensePlate() + " parked successfully at Floor " + floorNumber + ", Spot " + spot.getSpotNumber());
        } else {
            System.out.println("Sorry, no available spot for vehicle " + vehicle.getLicensePlate());
        }
    }

    public void unparkVehicle(Vehicle vehicle) {
        // Find the vehicle's spot and unpark it
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot.isOccupied() && spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
                    spot.unpark();
                    System.out.println("Vehicle " + vehicle.getLicensePlate() + " unparked successfully.");
                    // In a real system, you would also process the ticket for payment here
                    return;
                }
            }
        }
        System.out.println("Could not find vehicle " + vehicle.getLicensePlate());
    }
}
