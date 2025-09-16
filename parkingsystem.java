import java.util.*;

// ===== Vehicle =====
abstract class Vehicle {
    private String plate;
    public Vehicle(String plate) { this.plate = plate; }
    public String getPlate() { return plate; }
}
class Car extends Vehicle { public Car(String plate) { super(plate); } }
class Bike extends Vehicle { public Bike(String plate) { super(plate); } }
class Truck extends Vehicle { public Truck(String plate) { super(plate); } }

// ===== Parking Spot =====
abstract class ParkingSpot {
    private int id;
    private boolean free = true;
    private Vehicle vehicle;

    public ParkingSpot(int id) { this.id = id; }
    public int getId() { return id; }
    public boolean isFree() { return free; }
    public void assignVehicle(Vehicle v) { this.vehicle = v; this.free = false; }
    public void removeVehicle() { this.vehicle = null; this.free = true; }
    public Vehicle getVehicle() { return vehicle; }
}
class CarSpot extends ParkingSpot { public CarSpot(int id) { super(id); } }
class BikeSpot extends ParkingSpot { public BikeSpot(int id) { super(id); } }
class TruckSpot extends ParkingSpot { public TruckSpot(int id) { super(id); } }

// ===== Billing Strategy =====
interface BillingStrategy {
    double calculateFee(long entry, long exit);
}
class HourlyBilling implements BillingStrategy {
    public double calculateFee(long entry, long exit) {
        long hours = Math.max(1, (exit - entry));
        return hours * 10.0; // ₹10 per hour
    }
}
class DailyBilling implements BillingStrategy {
    public double calculateFee(long entry, long exit) {
        long days = Math.max(1, (exit - entry) / 24);
        return days * 100.0; // ₹100 per day
    }
}

// ===== Ticket =====
class Ticket {
    private Vehicle vehicle;
    private ParkingSpot spot;
    private long entryTime;
    private long exitTime;
    private BillingStrategy billingStrategy;

    public Ticket(Vehicle v, ParkingSpot s, long entryTime, BillingStrategy strategy) {
        this.vehicle = v;
        this.spot = s;
        this.entryTime = entryTime;
        this.billingStrategy = strategy;
    }

    public void exit(long exitTime) {
        this.exitTime = exitTime;
        spot.removeVehicle();
    }

    public double getFee() {
        return billingStrategy.calculateFee(entryTime, exitTime);
    }

    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
}

// ===== Parking Lot Manager =====
class ParkingLotManager {
    private List<ParkingSpot> carSpots = new ArrayList<>();
    private List<ParkingSpot> bikeSpots = new ArrayList<>();
    private List<ParkingSpot> truckSpots = new ArrayList<>();

    public ParkingLotManager(int carCount, int bikeCount, int truckCount) {
        for (int i = 1; i <= carCount; i++) carSpots.add(new CarSpot(i));
        for (int i = 1; i <= bikeCount; i++) bikeSpots.add(new BikeSpot(i));
        for (int i = 1; i <= truckCount; i++) truckSpots.add(new TruckSpot(i));
    }

    public Ticket parkVehicle(Vehicle v, long entryTime, BillingStrategy strategy) {
        ParkingSpot spot = findFreeSpot(v);
        if (spot == null) {
            System.out.println("No spot available for " + v.getClass().getSimpleName());
            return null;
        }
        spot.assignVehicle(v);
        return new Ticket(v, spot, entryTime, strategy);
    }

    private ParkingSpot findFreeSpot(Vehicle v) {
        if (v instanceof Car) {
            for (ParkingSpot s : carSpots) if (s.isFree()) return s;
        } else if (v instanceof Bike) {
            for (ParkingSpot s : bikeSpots) if (s.isFree()) return s;
        } else if (v instanceof Truck) {
            for (ParkingSpot s : truckSpots) if (s.isFree()) return s;
        }
        return null;
    }
}

// ===== Demo =====
public class parkingsystem {
    public static void main(String[] args) {
        ParkingLotManager lot = new ParkingLotManager(2, 2, 1);

        Vehicle car1 = new Car("KA-01-1234");
        Vehicle bike1 = new Bike("KA-02-5678");
        Vehicle truck1 = new Truck("KA-03-9999");

        // Park vehicles
        Ticket t1 = lot.parkVehicle(car1, 1, new HourlyBilling());
        Ticket t2 = lot.parkVehicle(bike1, 2, new HourlyBilling());
        Ticket t3 = lot.parkVehicle(truck1, 5, new DailyBilling());

        // Exit vehicles
        if (t1 != null) {
            t1.exit(4); // 3 hours
            System.out.println("Car fee: " + t1.getFee());
        }
        if (t2 != null) {
            t2.exit(6); // 4 hours
            System.out.println("Bike fee: " + t2.getFee());
        }
        if (t3 != null) {
            t3.exit(29); // 1 day
            System.out.println("Truck fee: " + t3.getFee());
        }
    }
}
