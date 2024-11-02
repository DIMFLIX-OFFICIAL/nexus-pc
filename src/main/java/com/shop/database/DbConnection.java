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

import com.shop.helper.AlertHelper;

import javafx.scene.control.Alert;
import javafx.stage.Window;


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
    
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(createUsersTable);
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
