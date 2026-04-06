import java.util.*;

class Ticket {
    static int idCounter = 1;
    int ticketId;
    String name;
    String coachType;
    String status;

    Ticket(String name, String coachType, String status) {
        this.ticketId = idCounter++;
        this.name = name;
        this.coachType = coachType;
        this.status = status;
    }
}

class Coach {
    String type;
    int totalSeats = 60;
    int availableSeats = 60;
    int wlLimit = 10;
    int currentWL = 0;
    
    // Using LinkedHashMap to maintain booking order for the Chart
    Map<Integer, Ticket> allBookings = new LinkedHashMap<>();
    Queue<Ticket> waitingList = new LinkedList<>();

    Coach(String type) { this.type = type; }

    public void bookTicket(String name) {
        if (availableSeats > 0) {
            Ticket t = new Ticket(name, type, "Confirmed");
            allBookings.put(t.ticketId, t);
            availableSeats--;
            System.out.println("Success! ID: " + t.ticketId + " (Confirmed in " + type + ")");
        } else if (currentWL < wlLimit) {
            Ticket t = new Ticket(name, type, "Waiting List");
            waitingList.add(t);
            allBookings.put(t.ticketId, t);
            currentWL++;
            System.out.println("Added to WL! ID: " + t.ticketId + " (" + type + ")");
        } else {
            System.out.println("Booking Rejected: No capacity in " + type);
        }
    }

    public void cancelTicket(int id) {
        if (!allBookings.containsKey(id)) return;

        Ticket t = allBookings.get(id);
        if (t.status.equals("Confirmed")) {
            allBookings.remove(id);
            availableSeats++;
            System.out.println("Ticket " + id + " Cancelled.");
            
            // Promote from WL
            if (!waitingList.isEmpty()) {
                Ticket promoted = waitingList.poll();
                promoted.status = "Confirmed";
                availableSeats--;
                currentWL--;
                System.out.println("WL Ticket " + promoted.ticketId + " promoted to Confirmed.");
            }
        } else {
            // Cancel directly from WL
            allBookings.remove(id);
            waitingList.removeIf(tick -> tick.ticketId == id);
            currentWL--;
            System.out.println("Waiting List Ticket " + id + " Cancelled.");
        }
    }

    public void displayStatus() {
        System.out.println(type + " -> Available: " + availableSeats + " | WL: " + currentWL);
    }

    public void printCoachChart() {
        System.out.println("\n--- " + type + " Passenger List ---");
        System.out.printf("%-10s %-15s %-15s\n", "ID", "Name", "Status");
        for (Ticket t : allBookings.values()) {
            System.out.printf("%-10d %-15s %-15s\n", t.ticketId, t.name, t.status);
        }
    }
}

public class ZohoRailwayApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Coach[] coaches = {
            new Coach("AC Coach"),
            new Coach("Non-AC Coach"),
            new Coach("Seater")
        };

        while (true) {
            System.out.println("\n[1] Book [2] Check Availability [3] Cancel [4] Prepare Chart [5] Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();

            if (choice == 5) break;

            switch (choice) {
                case 1:
                    System.out.println("1.AC  2.Non-AC  3.Seater");
                    int type = sc.nextInt();
                    System.out.print("Passenger Name: ");
                    String name = sc.next();
                    if (type >= 1 && type <= 3) coaches[type-1].bookTicket(name);
                    break;

                case 2:
                    System.out.println("\n--- Availability ---");
                    for (Coach c : coaches) c.displayStatus();
                    break;

                case 3:
                    System.out.print("Enter Ticket ID: ");
                    int id = sc.nextInt();
                    for (Coach c : coaches) c.cancelTicket(id);
                    break;

                case 4:
                    System.out.println("\n========== FINAL RESERVATION CHART ==========");
                    for (Coach c : coaches) c.printCoachChart();
                    break;
                
                default:
                    System.out.println("Invalid Option.");
            }
        }
        sc.close();
    }
}