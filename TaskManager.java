import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Task {
    private static int count = 1;
    private final int id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private final LocalDate createdAt;

    public Task(String title, String description, String dueDate) {
        this.id = count++;
        this.title = title;
        this.description = description;
        this.status = "Pending";
        this.dueDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE);
        this.createdAt = LocalDate.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setDueDate(String dueDate) { this.dueDate = LocalDate.parse(dueDate, DateTimeFormatter.ISO_DATE); }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Title: %-20s | Status: %-10s | Due: %s | Created: %s\nDescription: %s",
            id, title, status, dueDate, createdAt, description
        );
    }
}

public class TaskManager {
    private static final List<Task> tasks = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "tasks.dat";

    public static void main(String[] args) {
        loadTasks();
        
        while (true) {
            displayMenu();
            int choice = getIntInput("Choose an option: ");
            
            switch (choice) {
                case 1 -> addTask();
                case 2 -> viewTasks();
                case 3 -> updateTask();
                case 4 -> deleteTask();
                case 5 -> markTaskComplete();
                case 6 -> searchTasks();
                case 7 -> {
                    saveTasks();
                    System.out.println("Exiting Task Management System...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n=== Task Management System ===");
        System.out.println("1. Add Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. Update Task");
        System.out.println("4. Delete Task");
        System.out.println("5. Mark Task as Complete");
        System.out.println("6. Search Tasks");
        System.out.println("7. Exit");
    }

    private static void addTask() {
        System.out.println("\n--- Add New Task ---");
        String title = getStringInput("Enter task title: ");
        String description = getStringInput("Enter task description: ");
        String dueDate = getDateInput("Enter due date (YYYY-MM-DD): ");
        
        tasks.add(new Task(title, description, dueDate));
        System.out.println("Task added successfully!");
    }

    private static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        System.out.println("\n--- All Tasks ---");
        System.out.println("1. View All");
        System.out.println("2. View Pending");
        System.out.println("3. View Completed");
        int viewChoice = getIntInput("Choose view option: ");

        System.out.println("\n=== Task List ===");
        tasks.stream()
            .filter(task -> viewChoice == 1 || 
                          (viewChoice == 2 && task.getStatus().equals("Pending")) ||
                          (viewChoice == 3 && task.getStatus().equals("Completed")))
            .forEach(System.out::println);
    }

    private static void updateTask() {
        viewTasks();
        if (tasks.isEmpty()) return;

        int id = getIntInput("\nEnter task ID to update: ");
        Task task = findTaskById(id);
        
        if (task != null) {
            System.out.println("\nCurrent Task Details:");
            System.out.println(task);
            
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Title");
            System.out.println("2. Description");
            System.out.println("3. Due Date");
            System.out.println("4. All Fields");
            
            int updateChoice = getIntInput("Choose field to update: ");
            
            switch (updateChoice) {
                case 1 -> task.setTitle(getStringInput("Enter new title: "));
                case 2 -> task.setDescription(getStringInput("Enter new description: "));
                case 3 -> task.setDueDate(getDateInput("Enter new due date (YYYY-MM-DD): "));
                case 4 -> {
                    task.setTitle(getStringInput("Enter new title: "));
                    task.setDescription(getStringInput("Enter new description: "));
                    task.setDueDate(getDateInput("Enter new due date (YYYY-MM-DD): "));
                }
                default -> {
                    System.out.println("Invalid choice. No changes made.");
                    return;
                }
            }
            System.out.println("Task updated successfully!");
        } else {
            System.out.println("Task not found.");
        }
    }

    private static void deleteTask() {
        viewTasks();
        if (tasks.isEmpty()) return;

        int id = getIntInput("\nEnter task ID to delete: ");
        if (tasks.removeIf(task -> task.getId() == id)) {
            System.out.println("Task deleted successfully!");
        } else {
            System.out.println("Task not found.");
        }
    }

    private static void markTaskComplete() {
        viewTasks();
        if (tasks.isEmpty()) return;

        int id = getIntInput("\nEnter task ID to mark as complete: ");
        Task task = findTaskById(id);
        
        if (task != null) {
            task.setStatus("Completed");
            System.out.println("Task marked as complete!");
        } else {
            System.out.println("Task not found.");
        }
    }

    private static void searchTasks() {
        System.out.println("\n--- Search Tasks ---");
        String keyword = getStringInput("Enter search keyword: ").toLowerCase();
        
        System.out.println("\n=== Search Results ===");
        tasks.stream()
            .filter(task -> task.getTitle().toLowerCase().contains(keyword) || 
                          task.getDescription().toLowerCase().contains(keyword))
            .forEach(System.out::println);
    }

    // Helper methods
    private static Task findTaskById(int id) {
        return tasks.stream()
                   .filter(task -> task.getId() == id)
                   .findFirst()
                   .orElse(null);
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static String getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
                return input;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    // File persistence methods
    private static void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadTasks() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                List<Task> loadedTasks = (List<Task>) ois.readObject();
                tasks.addAll(loadedTasks);
                Task.count = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading tasks: " + e.getMessage());
            }
        }
    }
}
