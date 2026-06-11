package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddMenu {
    public static void addItem(String name, double price, int stock) {
        String sql = "INSERT INTO menu (item_name, price, stock, status) "
                   + "VALUES (?, ?, ?, 'available')";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add error: " + e.getMessage());
        }
    }
}