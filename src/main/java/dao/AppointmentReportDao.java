
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentReportDao {

    public void displayMonthlySummary(String month) {
        String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN status = 'SCHEDULED' THEN 1 ELSE 0 END)
                        AS scheduled,
                    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END)
                        AS completed,
                    SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END)
                        AS cancelled
                FROM appointments
                WHERE substr(appointment_date, 1, 7) = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, month);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    System.out.println("\n==================================");
                    System.out.println("   MONTHLY APPOINTMENT SUMMARY");
                    System.out.println("==================================");
                    System.out.println("Month: " + month);
                    System.out.println("Total appointments: " + result.getInt("total"));
                    System.out.println("Scheduled: " + result.getInt("scheduled"));
                    System.out.println("Completed: " + result.getInt("completed"));
                    System.out.println("Cancelled: " + result.getInt("cancelled"));
                    System.out.println("==================================");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error generating monthly report: " + e.getMessage());
        }
    }
}
