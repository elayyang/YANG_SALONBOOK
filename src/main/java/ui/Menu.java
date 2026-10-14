
package ui;

import dao.ActivityLogDao;
import dao.AppointmentDao;
import dao.AppointmentReportDao;
import dao.ClientDao;
import dao.ServiceDao;
import dao.UserDao;

import model.Appointment;
import model.Client;
import model.Service;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner scanner = new Scanner(System.in);

    private final UserDao userDao = new UserDao();
    private final ClientDao clientDao = new ClientDao();
    private final ServiceDao serviceDao = new ServiceDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final AppointmentReportDao appointmentReportDao =
            new AppointmentReportDao();
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    private String currentUsername;

    // =========================
    // MAIN MENU
    // =========================
    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("          SALONBOOK");
            System.out.println(" Multi-Service Appointment Booking");
            System.out.println("==================================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    running = false;
                    System.out.println("Thank you for using SalonBook!");
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // =========================
    // REGISTRATION
    // =========================
    private void register() {
        System.out.println("\n--- ACCOUNT REGISTRATION ---");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        System.out.println("\nSelect your role:");
        System.out.println("1. Receptionist");
        System.out.println("2. Salon Staff");
        System.out.print("Choose a role: ");

        String choice = scanner.nextLine().trim();
        String role;

        switch (choice) {
            case "1" -> role = "RECEPTIONIST";
            case "2" -> role = "SALON STAFF";
            default -> {
                System.out.println("Invalid role selection.");
                return;
            }
        }

        userDao.register(username, password, role);
        activityLogDao.logActivity(username, "Submitted account registration");

        System.out.println(
                "Registration submitted. An administrator must approve your account."
        );
    }

    // =========================
    // LOGIN
    // =========================
    private void login() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println(
                    "Your account is pending approval or is not active."
            );
            return;
        }

        currentUsername = user.username();
        activityLogDao.logActivity(currentUsername, "Logged in");

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username() + "!");
        System.out.println("Role: " + user.roles());

        if ("ADMINISTRATOR".equalsIgnoreCase(user.roles())) {
            adminMenu();
        } else {
            staffMenu(user);
        }

        activityLogDao.logActivity(currentUsername, "Logged out");
        currentUsername = null;
    }

    // =========================
    // ADMINISTRATOR MENU
    // =========================
    private void adminMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==================================");
            System.out.println("      ADMINISTRATOR MENU");
            System.out.println("==================================");
            System.out.println("1. View Pending Accounts");
            System.out.println("2. Approve Account");
            System.out.println("3. Manage Clients");
            System.out.println("4. Manage Services");
            System.out.println("5. Manage Appointments");
            System.out.println("6. Monthly Appointment Summary");
            System.out.println("7. View Activity Logs");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> userDao.displayPendingUsers();
                case "2" -> approveAccount();
                case "3" -> clientMenu();
                case "4" -> serviceMenu();
                case "5" -> appointmentMenu();
                case "6" -> monthlyAppointmentSummary();
                case "7" -> {
                    activityLogDao.logActivity(
                            currentUsername, "Viewed activity logs");
                    activityLogDao.displayActivityLogs();
                }
                case "8" -> {
                    loggedIn = false;
                    System.out.println("Administrator logged out.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void approveAccount() {
        System.out.print("Enter user ID to approve: ");
        String input = scanner.nextLine().trim();

        try {
            int userId = Integer.parseInt(input);
            userDao.approveUser(userId);
            activityLogDao.logActivity(
                    currentUsername, "Attempted to approve user ID " + userId);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric user ID.");
        }
    }

    // =========================
    // STAFF MENU
    // =========================
    private void staffMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n==================================");
            System.out.println("           STAFF MENU");
            System.out.println("==================================");
            System.out.println("Logged in as: " + user.username());
            System.out.println("1. Manage Clients");
            System.out.println("2. Manage Services");
            System.out.println("3. Manage Appointments");
            System.out.println("4. Monthly Appointment Summary");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> clientMenu();
                case "2" -> serviceMenu();
                case "3" -> appointmentMenu();
                case "4" -> monthlyAppointmentSummary();
                case "5" -> {
                    loggedIn = false;
                    System.out.println("You have logged out.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // =========================
    // CLIENT MANAGEMENT
    // =========================
    private void clientMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- CLIENT MANAGEMENT ---");
            System.out.println("1. Add Client");
            System.out.println("2. View Clients");
            System.out.println("3. Update Client");
            System.out.println("4. Delete Client");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addClient();
                case "2" -> viewClients();
                case "3" -> updateClient();
                case "4" -> deleteClient();
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addClient() {
        System.out.print("Full name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Client name cannot be empty.");
            return;
        }

        clientDao.addClient(name, phone, email);
        activityLogDao.logActivity(
                currentUsername, "Added client: " + name);
    }

    private void viewClients() {
        System.out.println("\n--- CLIENT LIST ---");

        List<Client> clients = clientDao.getAllClients();

        if (clients.isEmpty()) {
            System.out.println("No clients found.");
            return;
        }

        for (Client client : clients) {
            System.out.println(
                    "ID: " + client.id()
                            + " | Name: " + client.fullName()
                            + " | Phone: " + client.phone()
                            + " | Email: " + client.email()
            );
        }
    }

    private void updateClient() {
        System.out.print("Enter client ID to update: ");
        String input = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(input);

            System.out.print("New full name: ");
            String name = scanner.nextLine().trim();

            System.out.print("New phone: ");
            String phone = scanner.nextLine().trim();

            System.out.print("New email: ");
            String email = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Client name cannot be empty.");
                return;
            }

            clientDao.updateClient(id, name, phone, email);
            activityLogDao.logActivity(
                    currentUsername, "Updated client ID " + id);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid client ID.");
        }
    }

    private void deleteClient() {
        System.out.print("Enter client ID to delete: ");
        String input = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(input);
            clientDao.deleteClient(id);
            activityLogDao.logActivity(
                    currentUsername, "Attempted to delete client ID " + id);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid client ID.");
        }
    }

    // =========================
    // SERVICE MANAGEMENT
    // =========================
    private void serviceMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- SERVICE MANAGEMENT ---");
            System.out.println("1. Add Service");
            System.out.println("2. View Services");
            System.out.println("3. Update Service");
            System.out.println("4. Delete Service");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addService();
                case "2" -> viewServices();
                case "3" -> updateService();
                case "4" -> deleteService();
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addService() {
        System.out.print("Service name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        try {
            System.out.print("Price (PHP): ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Duration in minutes: ");
            int duration = Integer.parseInt(scanner.nextLine().trim());

            if (name.isEmpty() || price < 0 || duration <= 0
                    || !Double.isFinite(price)) {
                System.out.println("Please enter valid service details.");
                return;
            }

            serviceDao.addService(name, description, price, duration);
            activityLogDao.logActivity(
                    currentUsername, "Added service: " + name);

        } catch (NumberFormatException e) {
            System.out.println("Price and duration must be valid numbers.");
        }
    }

    private void viewServices() {
        System.out.println("\n--- SERVICE LIST ---");

        List<Service> services = serviceDao.getAllServices();

        if (services.isEmpty()) {
            System.out.println("No services found.");
            return;
        }

        for (Service service : services) {
            System.out.println(
                    "ID: " + service.id()
                            + " | Service: " + service.serviceName()
                            + " | Description: " + service.description()
                            + " | Price: PHP " + service.price()
                            + " | Duration: " + service.durationMinutes() + " minutes"
            );
        }
    }

    private void updateService() {
        System.out.print("Enter service ID to update: ");
        String input = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(input);

            System.out.print("New service name: ");
            String name = scanner.nextLine().trim();

            System.out.print("New description: ");
            String description = scanner.nextLine().trim();

            System.out.print("New price (PHP): ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("New duration in minutes: ");
            int duration = Integer.parseInt(scanner.nextLine().trim());

            if (name.isEmpty() || price < 0 || duration <= 0
                    || !Double.isFinite(price)) {
                System.out.println("Please enter valid service details.");
                return;
            }

            serviceDao.updateService(id, name, description, price, duration);
            activityLogDao.logActivity(
                    currentUsername, "Updated service ID " + id);

        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numeric values.");
        }
    }

    private void deleteService() {
        System.out.print("Enter service ID to delete: ");
        String input = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(input);
            serviceDao.deleteService(id);
            activityLogDao.logActivity(
                    currentUsername, "Attempted to delete service ID " + id);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid service ID.");
        }
    }

    // =========================
    // APPOINTMENT MANAGEMENT
    // =========================
    private void appointmentMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- APPOINTMENT MANAGEMENT ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View Appointments");
            System.out.println("3. Update Appointment Status");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> bookAppointment();
                case "2" -> viewAppointments();
                case "3" -> updateAppointmentStatus();
                case "4" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void bookAppointment() {
        System.out.println("\n--- BOOK APPOINTMENT ---");

        viewClients();

        System.out.print("Enter client ID: ");
        String clientInput = scanner.nextLine().trim();

        viewServices();

        System.out.print("Enter service ID: ");
        String serviceInput = scanner.nextLine().trim();

        System.out.print("Appointment date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        System.out.print("Appointment time (HH:MM, 24-hour format): ");
        String time = scanner.nextLine().trim();

        try {
            int clientId = Integer.parseInt(clientInput);
            int serviceId = Integer.parseInt(serviceInput);

            LocalDate appointmentDate = LocalDate.parse(date);
            LocalTime appointmentTime = LocalTime.parse(time);

            if (clientId <= 0 || serviceId <= 0) {
                System.out.println("Client and service IDs must be positive numbers.");
                return;
            }

            if (appointmentDate.isBefore(LocalDate.now())) {
                System.out.println("The appointment date cannot be in the past.");
                return;
            }

            boolean added = appointmentDao.addAppointment(
                    clientId,
                    serviceId,
                    appointmentDate.toString(),
                    appointmentTime.toString()
            );

            if (added) {
                activityLogDao.logActivity(
                        currentUsername,
                        "Booked appointment for client ID " + clientId
                                + ", service ID " + serviceId
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Client and service IDs must be numbers.");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date or time format.");
        }
    }

    private void viewAppointments() {
        System.out.println("\n--- APPOINTMENT LIST ---");

        List<Appointment> appointments = appointmentDao.getAllAppointments();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.println(
                    "Appointment ID: " + appointment.id()
                            + " | Client ID: " + appointment.clientId()
                            + " | Service ID: " + appointment.serviceId()
                            + " | Date: " + appointment.appointmentDate()
                            + " | Time: " + appointment.appointmentTime()
                            + " | Status: " + appointment.status()
            );
        }
    }

    private void updateAppointmentStatus() {
        viewAppointments();

        System.out.print("Enter appointment ID: ");
        String input = scanner.nextLine().trim();

        try {
            int appointmentId = Integer.parseInt(input);

            System.out.println("1. SCHEDULED");
            System.out.println("2. COMPLETED");
            System.out.println("3. CANCELLED");
            System.out.print("Choose new status: ");

            String choice = scanner.nextLine().trim();
            String status;

            switch (choice) {
                case "1" -> status = "SCHEDULED";
                case "2" -> status = "COMPLETED";
                case "3" -> status = "CANCELLED";
                default -> {
                    System.out.println("Invalid status selection.");
                    return;
                }
            }

            boolean updated = appointmentDao.updateAppointmentStatus(
                    appointmentId, status);

            if (updated) {
                activityLogDao.logActivity(
                        currentUsername,
                        "Updated appointment ID " + appointmentId
                                + " to " + status
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid appointment ID.");
        }
    }

    // =========================
    // MONTHLY APPOINTMENT SUMMARY
    // =========================
    private void monthlyAppointmentSummary() {
        System.out.print("Enter month (YYYY-MM), for example 2026-10: ");
        String month = scanner.nextLine().trim();

        try {
            YearMonth parsedMonth = YearMonth.parse(month);
            appointmentReportDao.displayMonthlySummary(
                    parsedMonth.toString());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid month. Please use YYYY-MM.");
        }
    }
}
