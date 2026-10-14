
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityLogDao {

    public void logActivity(String username, String action) {
        String sql = """
                INSERT INTO activity_log
                (username, action, activity_time)
                VALUES (?, ?, datetime('now', 'localtime'))
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, action);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Could not record activity: " + e.getMessage());
        }
    }

    public void displayActivityLogs() {
        String sql = """
                SELECT id, username, action, activity_time
                FROM activity_log
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            System.out.println("\n========== ACTIVITY LOG ==========");

            boolean found = false;

            while (result.next()) {
                found = true;

                System.out.println(
                        "ID: " + result.getInt("id")
                                + " | User: " + result.getString("username")
                                + " | Action: " + result.getString("action")
                                + " | Time: " + result.getString("activity_time")
                );
            }

            if (!found) {
                System.out.println("No activity has been recorded yet.");
            }

            System.out.println("==================================");

        } catch (SQLException e) {
            System.out.println("Error displaying activity logs: " + e.getMessage());
        }
    }
}
