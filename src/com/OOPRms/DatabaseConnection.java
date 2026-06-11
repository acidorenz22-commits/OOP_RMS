package com.OOPRms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:OOP_RMS.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String createMenu = "CREATE TABLE IF NOT EXISTS menu ("
                          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                          + "item_name TEXT NOT NULL, "
                          + "price REAL NOT NULL, "
                          + "stock INTEGER NOT NULL DEFAULT 0, "
                          + "status TEXT NOT NULL DEFAULT 'available')";

        String createOrders = "CREATE TABLE IF NOT EXISTS orders ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "order_ref TEXT NOT NULL, "
                            + "item_name TEXT NOT NULL, "
                            + "quantity INTEGER NOT NULL, "
                            + "total REAL NOT NULL, "
                            + "order_time TEXT NOT NULL)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMenu);
            stmt.execute(createOrders);

            // Add new columns to existing DB if not present
            try { stmt.execute(
                "ALTER TABLE menu ADD COLUMN stock INTEGER NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {}
            try { stmt.execute(
                "ALTER TABLE orders ADD COLUMN order_ref TEXT NOT NULL DEFAULT ''");
            } catch (SQLException ignored) {}
            try { stmt.execute(
                "ALTER TABLE orders ADD COLUMN order_time TEXT NOT NULL DEFAULT ''");
            } catch (SQLException ignored) {}

            // Migrate old status values
            stmt.execute(
                "UPDATE menu SET status = 'available' WHERE status = 'active'");
            stmt.execute(
                "UPDATE menu SET status = 'out_of_stock' WHERE status = 'inactive'");

            System.out.println("Tables ready.");
            clearOrders();
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void clearOrders() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='orders'");
            System.out.println("Orders cleared.");
        } catch (SQLException e) {
            System.out.println("Error clearing orders: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
    }
}