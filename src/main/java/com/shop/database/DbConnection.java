package com.shop.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.database.models.User;
import com.shop.database.models.Processor;
import com.shop.database.models.GraphicsCard;
import com.shop.database.models.Motherboard;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.RAM;
import com.shop.database.models.Cooler;
import com.shop.database.models.Case;
import com.shop.database.models.Computer;


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
                "brand VARCHAR(50) NOT NULL, " +
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



    //==> USER CRUD
    // ==================================================================================================================================
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return users;
    }
    
    public boolean deleteUser(String username) {
        String deleteUser = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteUser)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        String updateUser = "UPDATE users SET email = ?, first_name = ?, last_name = ?, password = ?, role = ? WHERE username = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateUser)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getUsername());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }    







    //==> PROCESSOR CRUD
    // ==================================================================================================================================
    public List<Processor> getAllProcessors() {
        List<Processor> processors = new ArrayList<>();
        String query = "SELECT * FROM processors";
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Processor processor = new Processor();
                processor.setId(rs.getInt("id"));
                processor.setName(rs.getString("name"));
                processor.setBrand(rs.getString("brand"));
                processor.setCores(rs.getInt("cores"));
                processor.setThreads(rs.getInt("threads"));
                processor.setBaseClock(rs.getBigDecimal("base_clock"));
                processor.setBoostClock(rs.getBigDecimal("boost_clock"));
                processor.setLink(rs.getString("link"));
                processors.add(processor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processors;
    }
    
    public boolean deleteProcessor(int id) {
        String deleteProcessor = "DELETE FROM processors WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteProcessor)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addProcessor(Processor processor) {
        String insertProcessor = "INSERT INTO processors (name, brand, cores, threads, base_clock, boost_clock, link) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertProcessor)) {
            pstmt.setString(1, processor.getName());
            pstmt.setString(2, processor.getBrand());
            pstmt.setInt(3, processor.getCores());
            pstmt.setInt(4, processor.getThreads());
            pstmt.setBigDecimal(5, processor.getBaseClock());
            pstmt.setBigDecimal(6, processor.getBoostClock());
            pstmt.setString(7, processor.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateProcessor(Processor processor) {
        String updateProcessor = "UPDATE processors SET name = ?, brand = ?, cores = ?, threads = ?, base_clock = ?, boost_clock = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateProcessor)) {
            pstmt.setString(1, processor.getName());
            pstmt.setString(2, processor.getBrand());
            pstmt.setInt(3, processor.getCores());
            pstmt.setInt(4, processor.getThreads());
            pstmt.setBigDecimal(5, processor.getBaseClock());
            pstmt.setBigDecimal(6, processor.getBoostClock());
            pstmt.setString(7, processor.getLink());
            pstmt.setInt(8, processor.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    

    //==> GRAPHICSCARD CRUD
    // ==================================================================================================================================
    public List<GraphicsCard> getAllGraphicsCards() {
        List<GraphicsCard> graphicsCards = new ArrayList<>();
        String query = "SELECT * FROM graphics_cards";
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
             while (rs.next()) {
                 GraphicsCard graphicsCard = new GraphicsCard();
                 graphicsCard.setId(rs.getInt("id"));
                 graphicsCard.setName(rs.getString("name"));
                 graphicsCard.setBrand(rs.getString("brand"));
                 graphicsCard.setMemorySize(rs.getInt("memory_size"));
                 graphicsCard.setMemoryType(rs.getString("memory_type"));
                 graphicsCard.setLink(rs.getString("link"));
                 graphicsCards.add(graphicsCard);
             }
         } catch (SQLException ex) {
             Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
         }
         return graphicsCards;
    }
    
    public boolean deleteGraphicsCard(int id) {
        String deleteGraphicsCard = "DELETE FROM graphics_cards WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteGraphicsCard)) {
             pstmt.setInt(1, id);
             return pstmt.executeUpdate() > 0;
         } catch (SQLException ex) {
             Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
             return false;
         }
    }
    
    public boolean addGraphicsCard(GraphicsCard graphicsCard) {
       String insertGraphicsCard = "INSERT INTO graphics_cards (name, brand, memory_size, memory_type, link) VALUES (?, ?, ?, ?, ?)";
       try (PreparedStatement pstmt = con.prepareStatement(insertGraphicsCard)) {
           pstmt.setString(1, graphicsCard.getName());
           pstmt.setString(2, graphicsCard.getBrand());
           pstmt.setInt(3, graphicsCard.getMemorySize());
           pstmt.setString(4, graphicsCard.getMemoryType());
           pstmt.setString(5, graphicsCard.getLink());
           return pstmt.executeUpdate() > 0;
       } catch (SQLException ex) {
           Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       }
    }
    
    public boolean updateGraphicsCard(GraphicsCard graphicsCard) {
       String updateGraphicsCard = "UPDATE graphics_cards SET name = ?, brand = ?, memory_size = ?, memory_type = ?, link = ? WHERE id = ?";
       try (PreparedStatement pstmt = con.prepareStatement(updateGraphicsCard)) {
           pstmt.setString(1, graphicsCard.getName());
              pstmt.setString(2, graphicsCard.getBrand());
              pstmt.setInt(3, graphicsCard.getMemorySize());
              pstmt.setString(4, graphicsCard.getMemoryType());
              pstmt.setString(5, graphicsCard.getLink());
              pstmt.setInt(6, graphicsCard.getId());
              return pstmt.executeUpdate() > 0;
       } catch (SQLException ex) {
           Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null ,ex);
           return false;
       }
    }   
    
    



    //==> POWERSUPPLY CRUD
    // ==================================================================================================================================
    public List<PowerSupply> getAllPowerSupplies() {
        List<PowerSupply> powerSupplies = new ArrayList<>();
        String query = "SELECT * FROM power_supplies";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                PowerSupply powerSupply = new PowerSupply();
                powerSupply.setId(rs.getInt("id"));
                powerSupply.setName(rs.getString("name"));
                powerSupply.setBrand(rs.getString("brand"));
                powerSupply.setWattage(rs.getInt("wattage"));
                powerSupply.setEfficiencyRating(rs.getString("efficiency_rating"));
                powerSupply.setLink(rs.getString("link"));
                powerSupplies.add(powerSupply);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return powerSupplies;
    }
    
    public boolean deletePowerSupply(int id) {
        String deletePowerSupply = "DELETE FROM power_supplies WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deletePowerSupply)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addPowerSupply(PowerSupply powerSupply) {
        String insertPowerSupply = "INSERT INTO power_supplies (name, brand, wattage, efficiency_rating, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertPowerSupply)) {
            pstmt.setString(1, powerSupply.getName());
            pstmt.setString(2, powerSupply.getBrand());
            pstmt.setInt(3, powerSupply.getWattage());
            pstmt.setString(4, powerSupply.getEfficiencyRating());
            pstmt.setString(5, powerSupply.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updatePowerSupply(PowerSupply powerSupply) {
        String updatePowerSupply = "UPDATE power_supplies SET name = ?, brand = ?, wattage = ?, efficiency_rating = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updatePowerSupply)) {
            pstmt.setString(1, powerSupply.getName());
            pstmt.setString(2, powerSupply.getBrand());
            pstmt.setInt(3, powerSupply.getWattage());
            pstmt.setString(4, powerSupply.getEfficiencyRating());
            pstmt.setString(5, powerSupply.getLink());
            pstmt.setInt(6, powerSupply.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }



    //==> RAM CRUD
    // ==================================================================================================================================
    public List<RAM> getAllRAMs() {
        List<RAM> rams = new ArrayList<>();
        String query = "SELECT * FROM ram";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RAM ram = new RAM();
                ram.setId(rs.getInt("id"));
                ram.setName(rs.getString("name"));
                ram.setBrand(rs.getString("brand"));
                ram.setCapacity(rs.getInt("capacity")); // in GB
                ram.setSpeed(rs.getInt("speed")); // in MHz
                ram.setLink(rs.getString("link"));
                rams.add(ram);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rams;
    }
    
    public boolean deleteRAM(int id) {
        String deleteRAM = "DELETE FROM ram WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteRAM)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addRAM(RAM ram) {
        String insertRAM = "INSERT INTO ram (name, brand, capacity, speed, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertRAM)) {
            pstmt.setString(1, ram.getName());
            pstmt.setString(2, ram.getBrand());
            pstmt.setInt(3, ram.getCapacity());
            pstmt.setInt(4, ram.getSpeed());
            pstmt.setString(5, ram.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateRAM(RAM ram) {
        String updateRAM = "UPDATE ram SET name = ?, brand = ?, capacity = ?, speed = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateRAM)) {
            pstmt.setString(1, ram.getName());
            pstmt.setString(2, ram.getBrand());
            pstmt.setInt(3, ram.getCapacity());
            pstmt.setInt(4, ram.getSpeed());
            pstmt.setString(5, ram.getLink());
            pstmt.setInt(6, ram.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    



    //==> MOTHERBOARD CRUD
    // ==================================================================================================================================
    public List<Motherboard> getAllMotherboards() {
        List<Motherboard> motherboards = new ArrayList<>();
        String query = "SELECT * FROM motherboards";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Motherboard motherboard = new Motherboard();
                motherboard.setId(rs.getInt("id"));
                motherboard.setName(rs.getString("name"));
                motherboard.setBrand(rs.getString("brand"));
                motherboard.setSocketType(rs.getString("socket_type"));
                motherboard.setFormFactor(rs.getString("form_factor"));
                motherboard.setMaxMemory(rs.getInt("max_memory"));
                motherboard.setLink(rs.getString("link"));
                motherboards.add(motherboard);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return motherboards;
    }
    
    public boolean deleteMotherboard(int id) {
        String deleteMotherboard = "DELETE FROM motherboards WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteMotherboard)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addMotherboard(Motherboard motherboard) {
        String insertMotherboard = "INSERT INTO motherboards (name, brand, socket_type, form_factor, max_memory, link) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertMotherboard)) {
            pstmt.setString(1, motherboard.getName());
            pstmt.setString(2, motherboard.getBrand());
            pstmt.setString(3, motherboard.getSocketType());
            pstmt.setString(4, motherboard.getFormFactor());
            pstmt.setInt(5, motherboard.getMaxMemory());
            pstmt.setString(6, motherboard.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateMotherboard(Motherboard motherboard) {
        String updateMotherboard = "UPDATE motherboards SET name = ?, brand = ?, socket_type = ?, form_factor = ?, max_memory = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateMotherboard)) {
            pstmt.setString(1, motherboard.getName());
            pstmt.setString(2, motherboard.getBrand());
            pstmt.setString(3, motherboard.getSocketType());
            pstmt.setString(4, motherboard.getFormFactor());
            pstmt.setInt(5, motherboard.getMaxMemory());
               pstmt.setString(6,motherboard .getLink()); 
            pstmt .setInt (7,motherboard .getId()); 
            return	pstmt.executeUpdate()>0; 
        } catch(SQLException	ex){ 
            Logger .getLogger(DbConnection.class .getName()).log(Level.SEVERE,null ,ex); 
            return	false; 
        } 
    }    




    //==> COOLER CRUD
    // ==================================================================================================================================
    public List<Cooler> getAllCoolers() {
        List<Cooler> coolers = new ArrayList<>();
        String query = "SELECT * FROM coolers";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cooler cooler = new Cooler();
                cooler.setId(rs.getInt("id"));
                cooler.setName(rs.getString("name"));
                cooler.setBrand(rs.getString("brand"));
                cooler.setType(rs.getString("type"));
                cooler.setCoolingCapacity(rs.getBigDecimal("cooling_capacity"));
                cooler.setLink(rs.getString("link"));
                coolers.add(cooler);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return coolers;
    }
    
    public boolean deleteCooler(int id) {
        String deleteCooler = "DELETE FROM coolers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteCooler)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addCooler(Cooler cooler) {
        String insertCooler = "INSERT INTO coolers (name, brand, type, cooling_capacity, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertCooler)) {
            pstmt.setString(1, cooler.getName());
            pstmt.setString(2, cooler.getBrand());
            pstmt.setString(3, cooler.getType());
            pstmt.setBigDecimal(4, cooler.getCoolingCapacity());
            pstmt.setString(5, cooler.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateCooler(Cooler cooler) {
        String updateCooler = "UPDATE coolers SET name = ?, brand = ?, type = ?, cooling_capacity = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateCooler)) {
            pstmt.setString(1, cooler.getName());
            pstmt.setString(2, cooler.getBrand());
            pstmt.setString(3, cooler.getType());
            pstmt.setBigDecimal(4, cooler.getCoolingCapacity());
            pstmt.setString(5, cooler.getLink());
            pstmt.setInt(6, cooler.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }



    //==> CASE CRUD
    // ==================================================================================================================================
    public List<Case> getAllCases() {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT * FROM cases";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Case computerCase = new Case();
                computerCase.setId(rs.getInt("id"));
                computerCase.setName(rs.getString("name"));
                computerCase.setBrand(rs.getString("brand"));
                computerCase.setFormFactor(rs.getString("form_factor"));
                computerCase.setColor(rs.getString("color"));
                computerCase.setLink(rs.getString("link"));
                cases.add(computerCase);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cases;
    }
    
    public boolean deleteCase(int id) {
        String deleteCase = "DELETE FROM cases WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteCase)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addCase(Case computerCase) {
        String insertCase = "INSERT INTO cases (name, brand, form_factor, color, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertCase)) {
            pstmt.setString(1, computerCase.getName());
            pstmt.setString(2, computerCase.getBrand());
            pstmt.setString(3, computerCase.getFormFactor());
            pstmt.setString(4, computerCase.getColor());
            pstmt.setString(5, computerCase.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateCase(Case computerCase) {
        String updateCase = "UPDATE cases SET name = ?, brand = ?, form_factor = ?, color = ?, link = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateCase)) {
            pstmt.setString(1, computerCase.getName());
            pstmt.setString(2, computerCase.getBrand());
            pstmt.setString(3, computerCase.getFormFactor());
            pstmt.setString(4, computerCase.getColor());
            pstmt.setString(5 ,computerCase .getLink()); 
            pstmt.setInt (6 ,computerCase .getId()); 
            return	pstmt.executeUpdate()>0; 
        } catch(SQLException ex){ 
            Logger.getLogger(DbConnection.class .getName()).log(Level.SEVERE, null, ex); 
            return false; 
        } 
    }    
    


    //==> COMPUTER CRUD
    // ==================================================================================================================================
    public List<Computer> getAllComputers() {
        List<Computer> computers = new ArrayList<>();
        String query = "SELECT * FROM computers";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Computer computer = new Computer();
                computer.setId(rs.getInt("id"));
                computer.setName(rs.getString("name"));
                computer.setDescription(rs.getString("description"));
                computer.setPrice(rs.getBigDecimal("price")); // Using BigDecimal for currency
                computer.setProcessorId(rs.getInt("processor_id"));
                computer.setGraphicsCardId(rs.getInt("graphics_card_id"));
                computer.setPowerSupplyId(rs.getInt("power_supply_id"));
                computer.setStockQuantity(rs.getInt("stock_quantity"));
                computer.setImagePath(rs.getString("image_path"));
                computers.add(computer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return computers;
    }
    
    public boolean deleteComputer(int id) {
        String deleteComputer = "DELETE FROM computers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteComputer)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean addComputer(Computer computer) {
        String insertComputer = "INSERT INTO computers (name, description, price, processor_id, graphics_card_id, power_supply_id, stock_quantity, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertComputer)) {
            pstmt.setString(1, computer.getName());
            pstmt.setString(2, computer.getDescription());
            pstmt.setBigDecimal(3, computer.getPrice());
            pstmt.setInt(4, computer.getProcessorId());
            pstmt.setInt(5, computer.getGraphicsCardId());
            pstmt.setInt(6, computer.getPowerSupplyId());
            pstmt.setInt(7, computer.getStockQuantity());
            pstmt.setString(8, computer.getImagePath());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean updateComputer(Computer computer) {
        String updateComputer = "UPDATE computers SET name = ?, description = ?, price = ?, processor_id = ?, graphics_card_id = ?, power_supply_id = ?, stock_quantity = ?, image_path = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateComputer)) {
            pstmt.setString(1, computer.getName());
               pstmt.setString(2 ,computer .getDescription()); 
            pstmt.setBigDecimal (3,computer .getPrice()); 
            pstmt.setInt (4, computer .getProcessorId()); 
            pstmt.setInt (5, computer .getGraphicsCardId()); 
            pstmt.setInt (6, computer .getPowerSupplyId()); 
            pstmt.setInt (7, computer .getStockQuantity()); 
            pstmt.setString (8, computer .getImagePath()); 
            pstmt.setInt (9, computer .getId()); 
            return	pstmt.executeUpdate()>0; 
        } catch(SQLException	ex){ 
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE,null ,ex); 
            return false; 
        } 
    }    
}   
