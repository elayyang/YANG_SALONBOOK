
package dao;

import model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    // CREATE: Add a salon service
    public boolean addService(
            String serviceName,
            String description,
            double price,
            int durationMinutes) {

        String sql = """
            INSERT INTO services
                (service_name, description, price, duration_minutes)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceName);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, durationMinutes);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding service: " + e.getMessage());
            return false;
        }
    }

    // READ: Get all salon services
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();

        String sql = """
            SELECT id, service_name, description, price, duration_minutes
            FROM services
            ORDER BY id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(
                        rs.getInt("id"),
                        rs.getString("service_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("duration_minutes")
                );

                services.add(service);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving services: " + e.getMessage());
        }

        return services;
    }

    // UPDATE: Edit an existing service
    public boolean updateService(
            int id,
            String serviceName,
            String description,
            double price,
            int durationMinutes) {

        String sql = """
            UPDATE services
            SET service_name = ?,
                description = ?,
                price = ?,
                duration_minutes = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serviceName);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, durationMinutes);
            pstmt.setInt(5, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error updating service: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Remove a service
    public boolean deleteService(int id) {
        String sql = "DELETE FROM services WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error deleting service: " + e.getMessage());
            return false;
        }
    }
}
