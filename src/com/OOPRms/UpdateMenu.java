package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateMenu {

    public static void updateItem(int id, String name, double price, int stock) {
        String status = stock > 0 ? "available" : "out_of_stock";

        String sql = "UPDATE menu SET item_name=?, price=?, stock=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);
            ps.setString(4, status); 
            ps.setInt(5, id);
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

    public static void deductStock(String itemName, int qty) {
        String deduct     = "UPDATE menu SET stock = stock - ? "
                          + "WHERE item_name = ? AND stock >= ?";
        String checkStock = "SELECT stock FROM menu WHERE item_name = ?";
        String setOOS     = "UPDATE menu SET status = 'out_of_stock' "
                          + "WHERE item_name = ? AND stock <= 0";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psDeduct = conn.prepareStatement(deduct);
             PreparedStatement psCheck  = conn.prepareStatement(checkStock);
             PreparedStatement psOOS    = conn.prepareStatement(setOOS)) {

            psDeduct.setInt(1, qty);
            psDeduct.setString(2, itemName);
            psDeduct.setInt(3, qty);
            psDeduct.executeUpdate();

            psCheck.setString(1, itemName);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                System.out.println(itemName + " stock remaining: "
                    + rs.getInt("stock"));
            }

            psOOS.setString(1, itemName);
            psOOS.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Deduct stock error: " + e.getMessage());
        }
    }
}