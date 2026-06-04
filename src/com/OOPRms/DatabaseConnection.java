package com.OOPRms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:OOP_RMS.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to OOP_RMS database successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String createMenu = "CREATE TABLE IF NOT EXISTS menu ("
                          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                          + "item_name TEXT NOT NULL, "
                          + "price REAL NOT NULL)";

        String createOrders = "CREATE TABLE IF NOT EXISTS orders ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "item_name TEXT NOT NULL, "
                            + "quantity INTEGER NOT NULL, "
                            + "total REAL NOT NULL)";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createMenu);
            stmt.execute(createOrders);
            System.out.println("Tables created successfully!");

            clearOrders();

        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void clearOrders() {
        String sql = "DELETE FROM orders";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='orders'");
            System.out.println("Orders cleared on startup!");

        } catch (SQLException e) {
            System.out.println("Error clearing orders: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables(); 
    }
}