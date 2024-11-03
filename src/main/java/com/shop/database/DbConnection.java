package com.shop.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DbConnection {
    private Connection con;
    private static DbConnection dbc;

    private DbConnection() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            FileInputStream fis = new FileInputStream("connection.prop");
            Properties p = new Properties();
            p.load(fis);
            con = DriverManager.getConnection((String) p.get("url"), (String) p.get("username"), (String) p.get("password"));
        } catch (Exception ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTablesIfNotExists() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(25) PRIMARY KEY NOT NULL, " +
                "email VARCHAR(45) NOT NULL, " +
                "first_name VARCHAR(25) NOT NULL, " +
                "last_name VARCHAR(25) NOT NULL, " +
                "password VARCHAR(64) NOT NULL, " +
                "role VARCHAR(25) NOT NULL DEFAULT 'user', " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createProcessorsTable = "CREATE TABLE IF NOT EXISTS processors (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "brand VARCHAR(50) NOT NULL, " +
                "cores INT NOT NULL, " +
                "threads INT NOT NULL, " +
                "base_clock DECIMAL(5,2) NOT NULL, " +
                "boost_clock DECIMAL(5,2) NOT NULL, " +
                "link VARCHAR(255) NOT NULL" +
                ");";

        String createGraphicsCardsTable = "CREATE TABLE IF NOT EXISTS graphics_cards (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "brand VARCHAR(50) NOT NULL, " +
                "memory_size INT NOT NULL, " +
                "memory_type VARCHAR(50) NOT NULL, " +
                "link VARCHAR(255) NOT NULL" +
                ");";

        String createPowerSuppliesTable = "CREATE TABLE IF NOT EXISTS power_supplies (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "wattage INT NOT NULL, " +
                "efficiency_rating VARCHAR(10) NOT NULL, " +
                "link VARCHAR(255) NOT NULL" +
                ");";

        String createMotherboardsTable = "CREATE TABLE IF NOT EXISTS motherboards (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "brand VARCHAR(50) NOT NULL, " +
                "socket_type VARCHAR(50) NOT NULL, " +
                "form_factor VARCHAR(50) NOT NULL, " +
                "max_memory INT NOT NULL, " +
                "link VARCHAR(255) NOT NULL" +
                ");";

        String createCoolersTable =  "CREATE TABLE IF NOT EXISTS coolers (" + 
                "id SERIAL PRIMARY KEY, "+ 
                "name VARCHAR(100) NOT NULL," + 
                "brand VARCHAR(50) NOT NULL," + 
                "type VARCHAR(50) NOT NULL," + 
                "cooling_capacity DECIMAL(5,2) NOT NULL," + 
                "link VARCHAR(255) NOT NULL" + 
                ");"; 

        String createCasesTable =  "CREATE TABLE IF NOT EXISTS cases (" + 
                "id SERIAL PRIMARY KEY," + 
                "name VARCHAR(100) NOT NULL," + 
                "brand VARCHAR(50) NOT NULL," + 
                "form_factor VARCHAR(50) NOT NULL," + 
                "color VARCHAR(30)," + 
                "link VARCHAR(255) NOT NULL" + 
                ");"; 

        String createRAMTable =  "CREATE TABLE IF NOT EXISTS ram (" + 
                "id SERIAL PRIMARY KEY," + 
                "name VARCHAR(100) NOT NULL," + 
                "brand VARCHAR(50) NOT NULL," + 
                "capacity INT NOT NULL," + // Объем памяти в ГБ
                "speed INT NOT NULL," + // Скорость в МГц
                "link VARCHAR(255) NOT NULL" + 
                ");";
        
        String createComputersTable = "CREATE TABLE IF NOT EXISTS computers (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "description TEXT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "processor_id INT NOT NULL REFERENCES processors(id), " +
                "graphics_card_id INT NOT NULL REFERENCES graphics_cards(id), " +
                "power_supply_id INT NOT NULL REFERENCES power_supplies(id), " +
                "stock_quantity INT NOT NULL, " +
                "image_path VARCHAR(255) NOT NULL" +
                ");";
    
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(createUsersTable);
            statement.executeUpdate(createProcessorsTable);
            statement.executeUpdate(createGraphicsCardsTable);
            statement.executeUpdate(createPowerSuppliesTable);
            statement.executeUpdate(createMotherboardsTable);
            statement.executeUpdate(createCoolersTable);
            statement.executeUpdate(createCasesTable);
            statement.executeUpdate(createRAMTable);
            statement.executeUpdate(createComputersTable);
        } catch (Exception ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DbConnection getDatabaseConnection() {
        if (dbc == null) {
            dbc = new DbConnection();
        }
        return dbc;
    }

    public Connection getConnection() {
        return con;
    }

    public boolean addUser(String firstName, String lastName, String email, String username, String password) {
        String insertUser = "INSERT INTO users (first_name, last_name, email, username, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertUser)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, username);
            pstmt.setString(5, password);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
