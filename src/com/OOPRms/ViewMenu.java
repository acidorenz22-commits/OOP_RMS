package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ViewMenu {

    // ✅ This is what Main.java needs — returns a List
    public static List<Object[]> getItems() {
        List<Object[]> items = new ArrayList<>();
        String sql = "SELECT * FROM menu";

        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("item_name");
                row[2] = rs.getDouble("price");
                items.add(row);
            }

        } catch (Exception e) {
            System.out.println("Error fetching items: " + e.getMessage());
        }

        return items;
    }

    // ✅ Keep this too if you still need console printing
    public static void viewItems() {
        for (Object[] row : getItems()) {
            System.out.println(row[0] + " | " + row[1] + " | " + row[2]);
        }
    }
}