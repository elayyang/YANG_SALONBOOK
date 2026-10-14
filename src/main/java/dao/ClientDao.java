
package dao;

import model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDao {

    // CREATE: Add a new client
    public boolean addClient(
            String fullName, String phone, String email) {

        String sql = """
            INSERT INTO clients (full_name, phone, email)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding client: "
                    + e.getMessage());
            return false;
        }
    }

    // READ: Get all clients
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();

        String sql = """
            SELECT id, full_name, phone, email
            FROM clients
            ORDER BY id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email")
                );

                clients.add(client);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving clients: "
                    + e.getMessage());
        }

        return clients;
    }

    // UPDATE: Edit an existing client
    public boolean updateClient(
            int id, String fullName, String phone, String email) {

        String sql = """
            UPDATE clients
            SET full_name = ?, phone = ?, email = ?
            WHERE id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setInt(4, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating client: "
                    + e.getMessage());
            return false;
        }
    }

    // DELETE: Remove a client
    public boolean deleteClient(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting client: "
                    + e.getMessage());
            return false;
        }
    }
}

