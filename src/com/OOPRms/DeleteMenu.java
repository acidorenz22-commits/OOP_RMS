package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteMenu {

    public static void deleteItem(int id) {

        String sql = "DELETE FROM menu WHERE id = ?";

        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);

            pstmt.executeUpdate();

            System.out.println("Menu item deleted!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}