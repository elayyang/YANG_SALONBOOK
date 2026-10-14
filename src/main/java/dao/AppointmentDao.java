
package dao;

import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    public boolean addAppointment(
            int clientId,
            int serviceId,
            String appointmentDate,
            String appointmentTime) {

        String sql = """
                INSERT INTO appointments
                (client_id, service_id, appointment_date,
                 appointment_time, status)
                VALUES (?, ?, ?, ?, 'SCHEDULED')
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Enable foreign-key checking for this connection.
            try (PreparedStatement pragma =
                         connection.prepareStatement("PRAGMA foreign_keys = ON")) {
                pragma.execute();
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, clientId);
                statement.setInt(2, serviceId);
                statement.setString(3, appointmentDate);
                statement.setString(4, appointmentTime);

                statement.executeUpdate();
                System.out.println("Appointment added successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Could not add appointment. Check the client ID, service ID, and database."
            );
            System.out.println("Details: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        String sql = """
                SELECT id, client_id, service_id, appointment_date,
                       appointment_time, status
                FROM appointments
                ORDER BY appointment_date, appointment_time
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Appointment appointment = new Appointment(
                        result.getInt("id"),
                        result.getInt("client_id"),
                        result.getInt("service_id"),
                        result.getString("appointment_date"),
                        result.getString("appointment_time"),
                        result.getString("status")
                );

                appointments.add(appointment);
            }

        } catch (SQLException e) {
            System.out.println("Error viewing appointments: " + e.getMessage());
        }

        return appointments;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        if (!status.equals("SCHEDULED")
                && !status.equals("COMPLETED")
                && !status.equals("CANCELLED")) {
            System.out.println("Invalid appointment status.");
            return false;
        }

        String sql = """
                UPDATE appointments
                SET status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, appointmentId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Appointment status updated successfully!");
                return true;
            } else {
                System.out.println("Appointment ID not found.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }
}
