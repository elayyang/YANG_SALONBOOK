
package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    // REGISTER ACCOUNT
    public boolean register(
            String username,
            String password,
            String role
    ) {
        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage() != null
                    && e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println("Username already exists.");
            } else {
                System.out.println("Registration error: " + e.getMessage());
            }

            return false;
        }
    }

    // LOGIN ACCOUNT
    public User login(String username, String password) {
        String sql = """
                SELECT id, username, password, roles, status
                FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    // DISPLAY PENDING ACCOUNTS
    public void displayPendingUsers() {
        String sql = """
                SELECT id, username, roles, status
                FROM users
                WHERE status = 'PENDING'
                ORDER BY id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n===== PENDING ACCOUNTS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("id")
                                + " | Username: " + rs.getString("username")
                                + " | Role: " + rs.getString("roles")
                                + " | Status: " + rs.getString("status")
                );
            }

            if (!found) {
                System.out.println("No pending accounts.");
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error displaying pending accounts: " + e.getMessage()
            );
        }
    }

    // APPROVE ACCOUNT
    public boolean approveUser(int userId) {
        String sql = """
                UPDATE users
                SET status = 'ACTIVE'
                WHERE id = ? AND status = 'PENDING'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                return true;
            }

            System.out.println(
                    "No pending account found with ID: " + userId
            );
            return false;

        } catch (SQLException e) {
            System.out.println(
                    "Approval error: " + e.getMessage()
            );
            return false;
        }
    }
}
