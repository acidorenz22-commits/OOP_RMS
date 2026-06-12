package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddMenu {
    public static void addItem(String name, double price, int stock) {

        String check = "SELECT id, stock FROM menu "
                     + "WHERE LOWER(item_name) = LOWER(?) AND price = ?";

        String update = "UPDATE menu SET stock = stock + ?, status = 'available' "
                      + "WHERE LOWER(item_name) = LOWER(?) AND price = ?";

        String insert = "INSERT INTO menu (item_name, price, stock, status) "
                      + "VALUES (?, ?, ?, 'available')";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psCheck = conn.prepareStatement(check)) {

            psCheck.setString(1, name);
            psCheck.setDouble(2, price);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                // Same item + same price exists — just update stock
                try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                    psUpdate.setInt(1, stock);
                    psUpdate.setString(2, name);
                    psUpdate.setDouble(3, price);
                    psUpdate.executeUpdate();
                    System.out.println("Stock updated for existing item: " + name);
                }
            } else {
                // New item — insert fresh
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setString(1, name);
                    psInsert.setDouble(2, price);
                    psInsert.setInt(3, stock);
                    psInsert.executeUpdate();
                    System.out.println("New item added: " + name);
                }
            }

        } catch (SQLException e) {
            System.out.println("Add error: " + e.getMessage());
        }
    }
}