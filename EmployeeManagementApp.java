import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Employee {
    int id;
    String name;
    int age;
    String designation;
    String department;
    String reportingTo; // Name of the manager

    Employee(int id, String name, int age, String designation, String department, String reportingTo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.designation = designation;
        this.department = department;
        this.reportingTo = reportingTo;
    }

    @Override
    public String toString() {
        return String.format("| %-3d | %-12s | %-3d | %-15s | %-12s | %-12s |", 
                id, name, age, designation, department, reportingTo);
    }
}

public class EmployeeManagementApp {
    private static List<Employee> employees = new ArrayList<>();

    public static void main(String[] args) {
        // Initializing with sample data as per Zoho requirements
        initializeData();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Employee Record Management ---");
            System.out.println("1. Display All Records");
            System.out.println("2. Search Records (Query Engine)");
            System.out.println("3. Print Reporting Tree");
            System.out.println("4. Print Subordinates");
            System.out.println("5. Organization Summary");
            System.out.println("6. Exit");
            System.out.print("Select Task: ");
            int task = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (task == 6) break;

            switch (task) {
                case 1: displayTable(employees); break;
                case 2: searchEngine(sc); break;
                case 3: 
                    System.out.print("Enter Employee Name: ");
                    printReportingTree(sc.nextLine()); 
                    break;
                case 4: 
                    System.out.print("Enter Manager Name: ");
                    printSubordinates(sc.nextLine()); 
                    break;
                case 5: printSummary(); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void initializeData() {
        employees.add(new Employee(1, "Alice", 45, "CEO", "Admin", "None"));
        employees.add(new Employee(2, "Bob", 40, "Manager", "Finance", "Alice"));
        employees.add(new Employee(3, "Charlie", 35, "Manager", "IT", "Alice"));
        employees.add(new Employee(4, "David", 30, "Developer", "IT", "Charlie"));
        employees.add(new Employee(5, "Eve", 28, "Developer", "IT", "Charlie"));
        employees.add(new Employee(6, "Frank", 32, "Analyst", "Finance", "Bob"));
    }

    private static void displayTable(List<Employee> list) {
        System.out.println("+-----+--------------+-----+-----------------+--------------+--------------+");
        System.out.println("| ID  | Name         | Age | Designation     | Department   | Reporting To |");
        System.out.println("+-----+--------------+-----+-----------------+--------------+--------------+");
        list.forEach(System.out::println);
        System.out.println("+-----+--------------+-----+-----------------+--------------+--------------+");
    }

    // Task 2: Advanced Search Engine with AND logic
    private static void searchEngine(Scanner sc) {
        System.out.println("Enter Query (e.g., 'age > 30 and department equals IT'):");
        String query = sc.nextLine().toLowerCase();
        String[] conditions = query.split(" and ");
        
        List<Employee> results = employees.stream().filter(emp -> {
            for (String cond : conditions) {
                if (!evaluateCondition(emp, cond)) return false;
            }
            return true;
        }).collect(Collectors.toList());

        displayTable(results);
    }

    private static boolean evaluateCondition(Employee e, String cond) {
        if (cond.contains("age >")) return e.age > Integer.parseInt(cond.split(">")[1].trim());
        if (cond.contains("age <")) return e.age < Integer.parseInt(cond.split("<")[1].trim());
        if (cond.contains("department equals")) return e.department.equalsIgnoreCase(cond.split("equals")[1].trim());
        if (cond.contains("department contains")) return e.department.toLowerCase().contains(cond.split("contains")[1].trim());
        return true; 
    }

    // Task 3: Reporting Tree (Hierarchy)
    private static void printReportingTree(String name) {
        Employee current = employees.stream().filter(e -> e.name.equalsIgnoreCase(name)).findFirst().orElse(null);
        if (current == null) return;
        
        System.out.print(current.name);
        String managerName = current.reportingTo;
        while (!managerName.equals("None")) {
            System.out.print(" -> " + managerName);
            String nextManager = managerName;
            managerName = employees.stream()
                .filter(e -> e.name.equalsIgnoreCase(nextManager))
                .findFirst()
                .map(e -> e.reportingTo)
                .orElse("None");
        }
        System.out.println();
    }

    // Task 4: Employees reporting to a manager
    private static void printSubordinates(String manager) {
        List<Employee> subs = employees.stream()
            .filter(e -> e.reportingTo.equalsIgnoreCase(manager))
            .collect(Collectors.toList());
        displayTable(subs);
    }

    // Task 5: Summary module
    private static void printSummary() {
        System.out.println("\n--- Department Summary ---");
        Map<String, Long> deptCount = employees.stream()
            .collect(Collectors.groupingBy(e -> e.department, Collectors.counting()));
        deptCount.forEach((k, v) -> System.out.println(k + ": " + v + " Employees"));
    }
}