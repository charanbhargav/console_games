import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class BankAccount {
    private final String accountNumber;
    private final String name;
    private double balance;
    private final List<String> transactionHistory = new ArrayList<>();

    BankAccount(String accountNumber, String name, double balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = balance;
        addHistory("Account created with balance: %.2f", balance);
    }

    String getAccountNumber() {
        return accountNumber;
    }

    String getName() {
        return name;
    }

    double getBalance() {
        return balance;
    }

    List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    void deposit(double amount) {
        balance += amount;
        addHistory("Deposited %.2f. New balance: %.2f", amount, balance);
    }

    boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        addHistory("Withdrew %.2f. New balance: %.2f", amount, balance);
        return true;
    }

    void transferOut(double amount, String targetAccount) {
        balance -= amount;
        addHistory("Transferred %.2f to account %s. New balance: %.2f", amount, targetAccount, balance);
    }

    void transferIn(double amount, String sourceAccount) {
        balance += amount;
        addHistory("Received %.2f from account %s. New balance: %.2f", amount, sourceAccount, balance);
    }

    private void addHistory(String format, Object... args) {
        transactionHistory.add(String.format(format, args));
    }
}

public class BankAccountSimulation {
    private static final Map<String, BankAccount> accounts = new LinkedHashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("=== Bank Account Simulation ===");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Account Details");
            System.out.println("5. Transfer Money");
            System.out.println("6. View Transaction History");
            System.out.println("7. Exit");

            int choice = readInt(scanner, "Enter your choice: ");

            switch (choice) {
                case 1 -> createAccount(scanner);
                case 2 -> depositMoney(scanner);
                case 3 -> withdrawMoney(scanner);
                case 4 -> viewAccountDetails(scanner);
                case 5 -> transferMoney(scanner);
                case 6 -> viewTransactionHistory(scanner);
                case 7 -> {
                    System.out.println("Exiting application.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }

    private static void createAccount(Scanner scanner) {
        String accountNumber = readNonEmptyString(scanner, "Enter account number: ");
        if (accounts.containsKey(accountNumber)) {
            System.out.println("An account with this number already exists.");
            return;
        }

        String name = readNonEmptyString(scanner, "Enter account holder name: ");
        double balance = readDoubleMin(scanner, "Enter initial balance: ", 0.0);

        BankAccount account = new BankAccount(accountNumber, name, balance);
        accounts.put(accountNumber, account);
        System.out.println("Account created successfully.");
    }

    private static void depositMoney(Scanner scanner) {
        BankAccount account = findAccount(scanner, "Enter account number: ");
        if (account == null) {
            return;
        }

        double amount = readDoubleMin(scanner, "Enter deposit amount: ", 0.01);
        account.deposit(amount);
        System.out.printf("Deposit successful. New balance: %.2f%n", account.getBalance());
    }

    private static void withdrawMoney(Scanner scanner) {
        BankAccount account = findAccount(scanner, "Enter account number: ");
        if (account == null) {
            return;
        }

        double amount = readDoubleMin(scanner, "Enter withdrawal amount: ", 0.01);
        if (!account.withdraw(amount)) {
            System.out.println("Withdrawal denied: insufficient balance.");
            return;
        }

        System.out.printf("Withdrawal successful. New balance: %.2f%n", account.getBalance());
    }

    private static void viewAccountDetails(Scanner scanner) {
        BankAccount account = findAccount(scanner, "Enter account number: ");
        if (account == null) {
            return;
        }

        printAccount(account);
    }

    private static void transferMoney(Scanner scanner) {
        BankAccount source = findAccount(scanner, "Enter source account number: ");
        if (source == null) {
            return;
        }

        BankAccount target = findAccount(scanner, "Enter target account number: ");
        if (target == null) {
            return;
        }

        if (source.getAccountNumber().equals(target.getAccountNumber())) {
            System.out.println("Source and target accounts must be different.");
            return;
        }

        double amount = readDoubleMin(scanner, "Enter transfer amount: ", 0.01);
        if (amount > source.getBalance()) {
            System.out.println("Transfer denied: insufficient balance.");
            return;
        }

        source.transferOut(amount, target.getAccountNumber());
        target.transferIn(amount, source.getAccountNumber());
        System.out.printf("Transfer successful. Source balance: %.2f, Target balance: %.2f%n",
                source.getBalance(), target.getBalance());
    }

    private static void viewTransactionHistory(Scanner scanner) {
        BankAccount account = findAccount(scanner, "Enter account number: ");
        if (account == null) {
            return;
        }

        System.out.println();
        System.out.println("Transaction History for Account " + account.getAccountNumber());
        System.out.println("----------------------------------------");
        List<String> history = account.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (String entry : history) {
            System.out.println(entry);
        }
    }

    private static void printAccount(BankAccount account) {
        System.out.println();
        System.out.println("Account Details");
        System.out.println("---------------");
        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Name: " + account.getName());
        System.out.printf("Balance: %.2f%n", account.getBalance());
    }

    private static BankAccount findAccount(Scanner scanner, String prompt) {
        String accountNumber = readNonEmptyString(scanner, prompt);
        BankAccount account = accounts.get(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
        }
        return account;
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

    private static double readDoubleMin(Scanner scanner, String prompt, double minimum) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= minimum) {
                    return value;
                }
                System.out.println("Please enter a value of at least " + String.format("%.2f", minimum) + ".");
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid amount.");
            }
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