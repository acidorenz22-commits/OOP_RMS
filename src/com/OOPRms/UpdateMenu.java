package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateMenu {
    public static void updateItem(int id, String name, double price, int stock) {
        String sql = "UPDATE menu SET item_name=?, price=?, stock=? WHERE id=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }
    }

    public static void updateStatus(int id, String status) {
        String sql = "UPDATE menu SET status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Status update error: " + e.getMessage());
        }
    }

    // Deduct stock when item is ordered
    public static void deductStock(String itemName, int qty) {
        String sql = "UPDATE menu SET stock = stock - ? "
                   + "WHERE item_name = ? AND stock >= ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setString(2, itemName);
            ps.setInt(3, qty);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Deduct stock error: " + e.getMessage());
        }
    }
}