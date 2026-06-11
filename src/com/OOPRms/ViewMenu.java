package com.OOPRms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ViewMenu {

    // All items for staff panel
    public static List<Object[]> getItems() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, item_name, price, stock, status FROM menu";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            System.out.println("View error: " + e.getMessage());
        }
        return list;
    }

    // Only available items for customer panel
    public static List<Object[]> getActiveItems() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, item_name, price FROM menu "
                   + "WHERE status = 'available'";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getDouble("price")
                });
            }
        } catch (Exception e) {
            System.out.println("View available error: " + e.getMessage());
        }
        return list;
    }
}