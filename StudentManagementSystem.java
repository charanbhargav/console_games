import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Student {
    private final int id;
    private String name;
    private final LinkedHashMap<String, Integer> subjectMarks;

    Student(int id, String name, LinkedHashMap<String, Integer> subjectMarks) {
        this.id = id;
        this.name = name;
        this.subjectMarks = new LinkedHashMap<>(subjectMarks);
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Map<String, Integer> getSubjectMarks() {
        return new LinkedHashMap<>(subjectMarks);
    }

    void setSubjectMarks(LinkedHashMap<String, Integer> updatedMarks) {
        subjectMarks.clear();
        subjectMarks.putAll(updatedMarks);
    }

    double getAverage() {
        if (subjectMarks.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (int marks : subjectMarks.values()) {
            total += marks;
        }
        return (double) total / subjectMarks.size();
    }

    String getGrade() {
        double average = getAverage();
        if (average >= 90) {
            return "A+";
        } else if (average >= 80) {
            return "A";
        } else if (average >= 70) {
            return "B";
        } else if (average >= 60) {
            return "C";
        } else if (average >= 50) {
            return "D";
        }
        return "F";
    }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s, Average: %.2f, Grade: %s", id, name, getAverage(), getGrade());
    }
}

public class StudentManagementSystem {
    private static final List<Student> students = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("=== Student Management System ===");
            System.out.println("1. Add New Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Update Student Record");
            System.out.println("5. Delete Student Record");
            System.out.println("6. Exit");

            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1 -> addStudent(scanner);
                case 2 -> displayAllStudents();
                case 3 -> searchStudentById(scanner);
                case 4 -> updateStudent(scanner);
                case 5 -> deleteStudent(scanner);
                case 6 -> {
                    System.out.println("Exiting application.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private static void addStudent(Scanner scanner) {
        int id = readInt(scanner, "Enter student ID: ");
        if (findStudentById(id) != null) {
            System.out.println("A student with this ID already exists.");
            return;
        }

        String name = readNonEmptyString(scanner, "Enter student name: ");
        LinkedHashMap<String, Integer> marks = readSubjectMarks(scanner);

        students.add(new Student(id, name, marks));
        System.out.println("Student added successfully.");
    }

    private static void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }

        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-20s %-35s %-10s %-10s%n", "ID", "Name", "Subjects", "Average", "Grade");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Student student : students) {
            System.out.printf("%-8d %-20s %-35s %-10.2f %-10s%n",
                    student.getId(),
                    student.getName(),
                    formatSubjects(student.getSubjectMarks()),
                    student.getAverage(),
                    student.getGrade());
        }

        System.out.println("------------------------------------------------------------------------------------------------");
    }

    private static void searchStudentById(Scanner scanner) {
        int id = readInt(scanner, "Enter student ID to search: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        printStudentDetails(student);
    }

    private static void updateStudent(Scanner scanner) {
        int id = readInt(scanner, "Enter student ID to update: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Leave the name blank to keep the current value.");
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            student.setName(newName);
        }

        System.out.print("Do you want to update marks? (y/n): ");
        String updateMarksChoice = scanner.nextLine().trim();
        if (updateMarksChoice.equalsIgnoreCase("y")) {
            student.setSubjectMarks(readSubjectMarks(scanner));
        }

        System.out.println("Student record updated successfully.");
    }

    private static void deleteStudent(Scanner scanner) {
        int id = readInt(scanner, "Enter student ID to delete: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        students.remove(student);
        System.out.println("Student record deleted successfully.");
    }

    private static Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    private static LinkedHashMap<String, Integer> readSubjectMarks(Scanner scanner) {
        int subjectCount = readInt(scanner, "Enter number of subjects: ");
        while (subjectCount <= 0) {
            System.out.println("Number of subjects must be greater than zero.");
            subjectCount = readInt(scanner, "Enter number of subjects: ");
        }

        LinkedHashMap<String, Integer> marks = new LinkedHashMap<>();
        for (int i = 1; i <= subjectCount; i++) {
            String subjectName = readNonEmptyString(scanner, "Enter subject " + i + " name: ");
            int mark = readIntInRange(scanner, "Enter marks for " + subjectName + " (0-100): ", 0, 100);
            marks.put(subjectName, mark);
        }
        return marks;
    }

    private static void printStudentDetails(Student student) {
        System.out.println();
        System.out.println("Student Details");
        System.out.println("---------------");
        System.out.println("ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("Subjects and Marks:");

        for (Map.Entry<String, Integer> entry : student.getSubjectMarks().entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.printf("Average Marks: %.2f%n", student.getAverage());
        System.out.println("Grade: " + student.getGrade());
    }

    private static String formatSubjects(Map<String, Integer> marks) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : marks.entrySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return builder.toString();
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid whole number.");
            }
        }
    }

    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a value between " + min + " and " + max + ".");
        }
    }

    private static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty.");
        }
    }
}