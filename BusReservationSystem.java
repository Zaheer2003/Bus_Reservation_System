import java.util.*;

public class BusReservationSystem {
    private HashMap<Integer, Customer> customers = new HashMap<>();
    private HashMap<String, Bus> buses = new HashMap<>();
    private HashMap<String, List<Reservation>> reservations = new HashMap<>();
    private HashMap<Integer, List<Reservation>> customerReservations = new HashMap<>();
    private Queue<Customer> waitingList = new LinkedList<>();
    private List<Reservation> allReservations = new ArrayList<>();
    private int reservationIdCounter = 1;

    public static void main(String[] args) {
        BusReservationSystem system = new BusReservationSystem();
        system.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Bus Reservation System");
        while (true) {
            System.out.println("1. Register Customer");
            System.out.println("2. Register Bus");
            System.out.println("3. Search Buses");
            System.out.println("4. Reserve Seat");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Request New Seat");
            System.out.println("7. Display All Reservations");
            System.out.println("8. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    registerCustomer(scanner);
                    break;
                case 2:
                    registerBus(scanner);
                    break;
                case 3:
                    searchBuses(scanner);
                    break;
                case 4:
                    reserveSeat(scanner);
                    break;
                case 5:
                    cancelReservation(scanner);
                    break;
                case 6:
                    requestNewSeat(scanner);
                    break;
                case 7:
                    displayAllReservations();
                    break;
                case 8:
                    System.out.println("Thank you for using Bus Reservation System");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void registerCustomer(Scanner scanner) {
        System.out.println("Enter Customer Name:");
        String name = scanner.nextLine().trim();
        System.out.println("Enter Mobile Number:");
        String mobileNumber = scanner.nextLine().trim();
        System.out.println("Enter Email ID:");
        String email = scanner.nextLine().trim();
        System.out.println("Enter City:");
        String city = scanner.nextLine().trim();
        System.out.println("Enter Age:");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        int customerId = customers.size() + 1;
        Customer customer = new Customer(customerId, name, mobileNumber, email, city, age);
        customers.put(customerId, customer);
        System.out.println("Customer registered successfully with ID: " + customerId);
    }

    private void registerBus(Scanner scanner) {
        System.out.println("Enter Bus Number:");
        String busNumber = scanner.nextLine().trim();
        System.out.println("Enter Total Seats:");
        int totalSeats = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("Enter Starting Point:");
        String startingPoint = scanner.nextLine().trim();
        System.out.println("Enter Ending Point:");
        String endingPoint = scanner.nextLine().trim();
        System.out.println("Enter Starting Time:");
        String startingTime = scanner.nextLine().trim();
        System.out.println("Enter Fare:");
        double fare = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        Bus bus = new Bus(busNumber, totalSeats, startingPoint, endingPoint, startingTime, fare);
        buses.put(busNumber, bus);
        System.out.println("Bus registered successfully with Number: " + busNumber);
    }

    private void searchBuses(Scanner scanner) {
        System.out.println("Enter Starting Point:");
        String startingPoint = scanner.nextLine().trim();
        System.out.println("Enter Ending Point:");
        String endingPoint = scanner.nextLine().trim();

        List<Bus> searchResults = new ArrayList<>();
        for (Bus bus : buses.values()) {
            if (bus.getStartingPoint().equalsIgnoreCase(startingPoint) && bus.getEndingPoint().equalsIgnoreCase(endingPoint)) {
                searchResults.add(bus);
            }
        }

        if (searchResults.isEmpty()) {
            System.out.println("No buses found.");
        } else {
            for (Bus bus : searchResults) {
                System.out.println(bus);
            }
        }
    }

    private void reserveSeat(Scanner scanner) {
        System.out.println("Enter Customer ID:");
        int customerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("Enter Bus Number:");
        String busNumber = scanner.nextLine().trim();

        if (!customers.containsKey(customerId)) {
            System.out.println("Customer not found.");
            return;
        }
        if (!buses.containsKey(busNumber)) {
            System.out.println("Bus not found.");
            return;
        }

        Bus bus = buses.get(busNumber);
        List<Reservation> busReservations = reservations.getOrDefault(busNumber, new ArrayList<>());

        if (busReservations.size() >= bus.getTotalSeats()) {
            System.out.println("No available seats. Adding customer to waiting list.");
            waitingList.add(customers.get(customerId));
            return;
        }

        Reservation reservation = new Reservation(reservationIdCounter++, busNumber, customerId, busReservations.size() + 1, new Date().toString());
        busReservations.add(reservation);
        reservations.put(busNumber, busReservations);
        allReservations.add(reservation);

        List<Reservation> customerResList = customerReservations.getOrDefault(customerId, new ArrayList<>());
        customerResList.add(reservation);
        customerReservations.put(customerId, customerResList);

        System.out.println("Seat reserved successfully. Reservation ID: " + reservation.getReservationId());
    }

    private void cancelReservation(Scanner scanner) {
        System.out.println("Enter Reservation ID:");
        int reservationId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        Reservation reservationToRemove = null;
        for (Reservation reservation : allReservations) {
            if (reservation.getReservationId() == reservationId) {
                reservationToRemove = reservation;
                break;
            }
        }

        if (reservationToRemove == null) {
            System.out.println("Reservation not found.");
            return;
        }

        String busNumber = reservationToRemove.getBusNumber();
        int customerId = reservationToRemove.getCustomerId();

        reservations.get(busNumber).remove(reservationToRemove);
        customerReservations.get(customerId).remove(reservationToRemove);
        allReservations.remove(reservationToRemove);

        System.out.println("Reservation cancelled successfully.");

        if (!waitingList.isEmpty()) {
            Customer nextCustomer = waitingList.poll();
            reserveSeat(nextCustomer.getCustomerId(), busNumber);
        }
    }

    private void requestNewSeat(Scanner scanner) {
        System.out.println("Enter Customer ID:");
        int customerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("Enter Bus Number:");
        String busNumber = scanner.nextLine().trim();

        if (!customers.containsKey(customerId)) {
            System.out.println("Customer not found.");
            return;
        }
        if (!buses.containsKey(busNumber)) {
            System.out.println("Bus not found.");
            return;
        }

        waitingList.add(customers.get(customerId));
        System.out.println("Customer added to the waiting list.");
    }

    private void displayAllReservations() {
        if (allReservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            for (Reservation reservation : allReservations) {
                System.out.println(reservation);
            }
        }
    }

    private void reserveSeat(int customerId, String busNumber) {
        if (!customers.containsKey(customerId)) {
            System.out.println("Customer not found.");
            return;
        }
        if (!buses.containsKey(busNumber)) {
            System.out.println("Bus not found.");
            return;
        }

        Bus bus = buses.get(busNumber);
        List<Reservation> busReservations = reservations.getOrDefault(busNumber, new ArrayList<>());

        if (busReservations.size() >= bus.getTotalSeats()) {
            System.out.println("No available seats. Adding customer to waiting list.");
            waitingList.add(customers.get(customerId));
            return;
        }

        Reservation reservation = new Reservation(reservationIdCounter++, busNumber, customerId, busReservations.size() + 1, new Date().toString());
        busReservations.add(reservation);
        reservations.put(busNumber, busReservations);
        allReservations.add(reservation);

        List<Reservation> customerResList = customerReservations.getOrDefault(customerId, new ArrayList<>());
        customerResList.add(reservation);
        customerReservations.put(customerId, customerResList);

        System.out.println("Seat reserved successfully for customer ID " + customerId + ". Reservation ID: " + reservation.getReservationId());
    }
}

class Customer {
    private int customerId;
    private String name;
    private String mobileNumber;
    private String email;
    private String city;
    private int age;

    public Customer(int customerId, String name, String mobileNumber, String email, String city, int age) {
        this.customerId = customerId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.city = city;
        this.age = age;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", age=" + age +
                '}';
    }
}

class Bus {
    private String busNumber;
    private int totalSeats;
    private String startingPoint;
    private String endingPoint;
    private String startingTime;
    private double fare;

    public Bus(String busNumber, int totalSeats, String startingPoint, String endingPoint, String startingTime, double fare) {
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.startingTime = startingTime;
        this.fare = fare;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "busNumber='" + busNumber + '\'' +
                ", totalSeats=" + totalSeats +
                ", startingPoint='" + startingPoint + '\'' +
                ", endingPoint='" + endingPoint + '\'' +
                ", startingTime='" + startingTime + '\'' +
                ", fare=" + fare +
                '}';
    }
}

class Reservation {
    private int reservationId;
    private String busNumber;
    private int customerId;
    private int seatNumber;
    private String reservationTime;

    public Reservation(int reservationId, String busNumber, int customerId, int seatNumber, String reservationTime) {
        this.reservationId = reservationId;
        this.busNumber = busNumber;
        this.customerId = customerId;
        this.seatNumber = seatNumber;
        this.reservationTime = reservationTime;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", busNumber='" + busNumber + '\'' +
                ", customerId=" + customerId +
                ", seatNumber=" + seatNumber +
                ", reservationTime='" + reservationTime + '\'' +
                '}';
    }
}
