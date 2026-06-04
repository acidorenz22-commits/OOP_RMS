package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateMenu {

    public static void updateItem(int id, String newName, double newPrice) {

        String sql = "UPDATE menu SET item_name = ?, price = ? WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, newName);
            pstmt.setDouble(2, newPrice);
            pstmt.setInt(3, id);

            pstmt.executeUpdate();
            
            System.out.println("Menu item updated!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}