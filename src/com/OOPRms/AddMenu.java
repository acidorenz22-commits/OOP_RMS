package com.OOPRms;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddMenu {

    public static void addItem(String itemName, double price) {

        String sql = "INSERT INTO menu(item_name, price) VALUES(?, ?)";

        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, itemName);
            pstmt.setDouble(2, price);

            pstmt.executeUpdate();

            System.out.println("Menu item added!");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}