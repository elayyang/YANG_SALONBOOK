
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:salonbook.db";

    // =========================
    // DATABASE CONNECTION
    // =========================
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }

        Connection connection = DriverManager.getConnection(URL);

        // Enable foreign keys for every connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            connection.close();
            throw e;
        }

        return connection;
    }

    // =========================
    // USERS TABLE
    // =========================
    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL
                        CHECK (roles IN (
                            'ADMINISTRATOR',
                            'RECEPTIONIST',
                            'SALON STAFF'
                        )),
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        executeTableCreation(sql, "Users");
    }

    // =========================
    // CLIENTS TABLE
    // =========================
    public static void createClientTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS clients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    phone TEXT,
                    email TEXT
                )
                """;

        executeTableCreation(sql, "Clients");
    }

    // =========================
    // SERVICES TABLE
    // =========================
    public static void createServiceTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS services (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    service_name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL CHECK (price >= 0),
                    duration_minutes INTEGER NOT NULL
                        CHECK (duration_minutes > 0)
                )
                """;

        executeTableCreation(sql, "Services");
    }

    // =========================
    // APPOINTMENTS TABLE
    // =========================
    public static void createAppointmentTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    client_id INTEGER NOT NULL,
                    service_id INTEGER NOT NULL,
                    appointment_date TEXT NOT NULL,
                    appointment_time TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'SCHEDULED'
                        CHECK (status IN (
                            'SCHEDULED',
                            'COMPLETED',
                            'CANCELLED'
                        )),
                    FOREIGN KEY (client_id)
                        REFERENCES clients(id),
                    FOREIGN KEY (service_id)
                        REFERENCES services(id)
                )
                """;

        executeTableCreation(sql, "Appointments");
    }

    // =========================
    // ACTIVITY LOG TABLE
    // =========================
    public static void createActivityLogTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS activity_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    action TEXT NOT NULL,
                    activity_time TEXT NOT NULL
                )
                """;

        executeTableCreation(sql, "Activity Log");
    }

    // =========================
    // HELPER METHOD
    // =========================
    private static void executeTableCreation(
            String sql,
            String tableName) {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println(tableName + " table is ready.");

        } catch (SQLException e) {
            System.out.println(
                    "Error creating " + tableName + " table: "
                            + e.getMessage()
            );
        }
    }
}
