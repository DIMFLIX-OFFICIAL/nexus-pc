package com.shop.database;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.database.models.User;
import com.shop.helper.AlertHelper;
import com.shop.database.models.Processor;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Motherboard;
import com.shop.database.models.Order;
import com.shop.database.models.OrderItem;
import com.shop.database.models.OrderResult;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.RAM;
import com.shop.database.models.ShoppingCartItem;
import com.shop.database.models.Cooler;
import com.shop.database.models.Case;
import com.shop.database.models.Computer;


public class DbConnection {
    private Connection con;
    private static DbConnection dbc;

    public void createTablesIfNotExists() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY NOT NULL, " +
                "email TEXT NOT NULL, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role VARCHAR(25) NOT NULL DEFAULT 'user', " +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createProcessorsTable = "CREATE TABLE IF NOT EXISTS processors (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " +
                "cores INT NOT NULL, " +
                "threads INT NOT NULL, " +
                "base_clock DECIMAL(5,2) NOT NULL, " +
                "boost_clock DECIMAL(5,2) NOT NULL, " +
                "link TEXT NOT NULL" +
                ");";

        String createGraphicsCardsTable = "CREATE TABLE IF NOT EXISTS graphic_cards (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " +
                "memory_size INT NOT NULL, " +
                "memory_type TEXT NOT NULL, " +
                "link TEXT NOT NULL" +
                ");";

        String createPowerSuppliesTable = "CREATE TABLE IF NOT EXISTS power_supplies (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " +
                "wattage INT NOT NULL, " +
                "efficiency_rating TEXT NOT NULL, " +
                "link TEXT NOT NULL" +
                ");";

        String createMotherboardsTable = "CREATE TABLE IF NOT EXISTS motherboards (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "brand TEXT NOT NULL, " +
                "socket_type TEXT NOT NULL, " +
                "form_factor TEXT NOT NULL, " +
                "max_memory INT NOT NULL, " +
                "link TEXT NOT NULL" +
                ");";

        String createCoolersTable =  "CREATE TABLE IF NOT EXISTS coolers (" + 
                "id SERIAL PRIMARY KEY, "+ 
                "name TEXT NOT NULL," + 
                "brand TEXT NOT NULL," + 
                "type TEXT NOT NULL," + 
                "cooling_capacity DECIMAL(5,2) NOT NULL," + 
                "link TEXT NOT NULL" + 
                ");"; 

        String createCasesTable =  "CREATE TABLE IF NOT EXISTS cases (" + 
                "id SERIAL PRIMARY KEY," + 
                "name TEXT NOT NULL," + 
                "brand TEXT NOT NULL," + 
                "form_factor TEXT NOT NULL," + 
                "color TEXT," + 
                "link TEXT NOT NULL" + 
                ");"; 

        String createRAMTable =  "CREATE TABLE IF NOT EXISTS rams (" + 
                "id SERIAL PRIMARY KEY," + 
                "name TEXT NOT NULL," + 
                "brand TEXT NOT NULL," + 
                "capacity INT NOT NULL," + // Объем памяти в ГБ
                "speed INT NOT NULL," + // Скорость в МГц
                "link TEXT NOT NULL" + 
                ");";
        
        String createComputersTable = "CREATE TABLE IF NOT EXISTS computers (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "processor_id INT NOT NULL REFERENCES processors(id), " +
                "graphic_card_id INT NOT NULL REFERENCES graphic_cards(id), " +
                "motherboard_id INT NOT NULL REFERENCES motherboards(id), " +
                "ram_id INT NOT NULL REFERENCES rams(id), " +
                "rams_count INT NOT NULL, " +
                "power_supply_id INT NOT NULL REFERENCES power_supplies(id), " +
                "cooler_id INT NOT NULL REFERENCES coolers(id), " +
                "case_id INT NOT NULL REFERENCES cases(id), " +
                "image_url TEXT NOT NULL, " +
                "stock_quantity INT NOT NULL DEFAULT 0" +
                ");";

        String createShoppingCartTable = "CREATE TABLE IF NOT EXISTS shopping_cart (" +
                "owner TEXT NOT NULL REFERENCES users(username), " +
                "computer_id INT NOT NULL REFERENCES computers(id), " +
                "quantity INT NOT NULL DEFAULT 1" +
                ");";

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "id SERIAL PRIMARY KEY, " +
                "customer TEXT NOT NULL REFERENCES users(username), " +
                "order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "total_amount DECIMAL(10,2) NOT NULL, " +
                "status VARCHAR(25) NOT NULL DEFAULT 'pending'" +
                ");";

        String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items (" +
                "id SERIAL PRIMARY KEY, " +
                "order_id INT NOT NULL REFERENCES orders(id), " +
                "computer_id INT NOT NULL REFERENCES computers(id), " +
                "quantity INT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL" +
                ");";

        String createOrderFunction = "CREATE OR REPLACE FUNCTION update_order_total() " +
                "RETURNS TRIGGER AS $$ " +
                "BEGIN " +
                "    UPDATE orders " +
                "    SET total_amount = ( " +
                "        SELECT COALESCE(SUM(price * quantity), 0) " +
                "        FROM order_items " +
                "        WHERE order_id = NEW.order_id " +
                "    ) " +
                "    WHERE id = NEW.order_id; " +
                "    RETURN NEW; " +
                "END; $$ LANGUAGE plpgsql;";

        String createOrderTrigger = "DO $$ BEGIN " +
                "IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'order_total_update') THEN " +
                "CREATE TRIGGER order_total_update " +
                "AFTER INSERT OR UPDATE OR DELETE ON order_items " +
                "FOR EACH ROW EXECUTE FUNCTION update_order_total(); " +
                "END IF; END $$;";

    
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
            statement.executeUpdate(createShoppingCartTable);
            statement.executeUpdate(createOrdersTable);
            statement.executeUpdate(createOrderItemsTable);
            statement.executeUpdate(createOrderFunction);
            statement.executeUpdate(createOrderTrigger);
        } catch (Exception ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
    }

    private DbConnection() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            FileInputStream fis = new FileInputStream("connection.prop");
            Properties p = new Properties();
            p.load(fis);
            con = DriverManager.getConnection((String) p.get("url"), (String) p.get("username"), (String) p.get("password"));
        } catch (Exception ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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


    //==> USER CRUD
    // ==================================================================================================================================
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );

                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return users;
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );
                return user;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteUser(String username) {
        String deleteUser = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteUser)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }    

    public boolean addUser(User user) {
        String insertUser = "INSERT INTO users (first_name, last_name, email, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getUsername());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
                Processor processor = new Processor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("cores"),
                    rs.getInt("threads"),
                    rs.getBigDecimal("base_clock"),
                    rs.getBigDecimal("boost_clock"),
                    rs.getString("link")
                );
                processors.add(processor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return processors;
    }

    public Processor getProcessorById(int id) {
        String query = "SELECT * FROM processors WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Processor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("cores"),
                    rs.getInt("threads"),
                    rs.getBigDecimal("base_clock"),
                    rs.getBigDecimal("boost_clock"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteProcessor(Integer id) {
        String deleteProcessor = "DELETE FROM processors WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteProcessor)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    

    //==> GRAPHICSCARD CRUD
    // ==================================================================================================================================
    public List<GraphicCard> getAllGraphicCards() {
        List<GraphicCard> graphicsCards = new ArrayList<>();
        String query = "SELECT * FROM graphic_cards";
        try (PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                 GraphicCard graphicsCard = new GraphicCard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("memory_size"),
                    rs.getString("memory_type"),
                    rs.getString("link")
                );
                graphicsCards.add(graphicsCard);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return graphicsCards;
    }

    public GraphicCard getGraphicCardById(int id) {
        String query = "SELECT * FROM graphic_cards WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new GraphicCard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("memory_size"),
                    rs.getString("memory_type"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteGraphicCard(Integer id) {
        String deleteGraphicsCard = "DELETE FROM graphic_cards WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteGraphicsCard)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    public boolean addGraphicCard(GraphicCard graphicsCard) {
        String insertGraphicsCard = "INSERT INTO graphic_cards (name, brand, memory_size, memory_type, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertGraphicsCard)) {
            pstmt.setString(1, graphicsCard.getName());
            pstmt.setString(2, graphicsCard.getBrand());
            pstmt.setInt(3, graphicsCard.getMemorySize());
            pstmt.setString(4, graphicsCard.getMemoryType());
            pstmt.setString(5, graphicsCard.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    public boolean updateGraphicCard(GraphicCard graphicsCard) {
        String updateGraphicsCard = "UPDATE graphic_cards SET name = ?, brand = ?, memory_size = ?, memory_type = ?, link = ? WHERE id = ?";
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
                PowerSupply powerSupply = new PowerSupply(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("wattage"),
                    rs.getString("efficiency_rating"),
                    rs.getString("link")
                );
                powerSupplies.add(powerSupply);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return powerSupplies;
    }

    public PowerSupply getPowerSupplyById(int id) {
        String query = "SELECT * FROM power_supplies WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PowerSupply(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("wattage"),
                    rs.getString("efficiency_rating"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deletePowerSupply(Integer id) {
        String deletePowerSupply = "DELETE FROM power_supplies WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deletePowerSupply)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }



    //==> RAM CRUD
    // ==================================================================================================================================
    public List<RAM> getAllRAMs() {
        List<RAM> rams = new ArrayList<>();
        String query = "SELECT * FROM rams";
        
        try (PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RAM ram = new RAM(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("capacity"), // in GB
                    rs.getInt("speed"), // in MHz
                    rs.getString("link")
                );
                rams.add(ram);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return rams;
    }

    public RAM getRAMById(int id) {
        String query = "SELECT * FROM rams WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new RAM(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getInt("capacity"), // in GB
                    rs.getInt("speed"), // in MHz
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteRAM(Integer id) {
        String deleteRAM = "DELETE FROM rams WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteRAM)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    public boolean addRAM(RAM ram) {
        String insertRAM = "INSERT INTO rams (name, brand, capacity, speed, link) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(insertRAM)) {
            pstmt.setString(1, ram.getName());
            pstmt.setString(2, ram.getBrand());
            pstmt.setInt(3, ram.getCapacity());
            pstmt.setInt(4, ram.getSpeed());
            pstmt.setString(5, ram.getLink());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    public boolean updateRAM(RAM ram) {
        String updateRAM = "UPDATE rams SET name = ?, brand = ?, capacity = ?, speed = ?, link = ? WHERE id = ?";
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
                Motherboard motherboard = new Motherboard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("socket_type"),
                    rs.getString("form_factor"),
                    rs.getInt("max_memory"),
                    rs.getString("link")
                );
                motherboards.add(motherboard);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return motherboards;
    }

    public Motherboard getMotherboardById(int id) {
        String query = "SELECT * FROM motherboards WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Motherboard(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("socket_type"),
                    rs.getString("form_factor"),
                    rs.getInt("max_memory"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteMotherboard(Integer id) {
        String deleteMotherboard = "DELETE FROM motherboards WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteMotherboard)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            return	pstmt.executeUpdate() > 0; 
        } catch(SQLException ex){ 
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
                Cooler cooler = new Cooler(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("type"),
                    rs.getBigDecimal("cooling_capacity"),
                    rs.getString("link")
                );
                coolers.add(cooler);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return coolers;
    }

    public Cooler getCoolerById(int id) {
        String query = "SELECT * FROM coolers WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Cooler(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("type"),
                    rs.getBigDecimal("cooling_capacity"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteCooler(Integer id) {
        String deleteCooler = "DELETE FROM coolers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteCooler)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
                Case computerCase = new Case(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("form_factor"),
                    rs.getString("color"),
                    rs.getString("link")
                );
                cases.add(computerCase);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return cases;
    }

    public Case getCaseById(int id) {
        String query = "SELECT * FROM cases WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Case(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("brand"),
                    rs.getString("form_factor"),
                    rs.getString("color"),
                    rs.getString("link")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }
    
    public boolean deleteCase(Integer id) {
        String deleteCase = "DELETE FROM cases WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteCase)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
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
                Computer computer = new Computer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getInt("processor_id"),
                    rs.getInt("graphic_card_id"),
                    rs.getInt("motherboard_id"),
                    rs.getInt("ram_id"),
                    rs.getInt("rams_count"),
                    rs.getInt("power_supply_id"),
                    rs.getInt("cooler_id"),
                    rs.getInt("case_id"),
                    rs.getString("image_url"),
                    rs.getInt("stock_quantity")
                );
                computers.add(computer);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return computers;
    }

    public Computer getComputerById(Integer id) {
        String query = "SELECT * FROM computers WHERE id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Computer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getInt("processor_id"),
                    rs.getInt("graphic_card_id"),
                    rs.getInt("motherboard_id"),
                    rs.getInt("ram_id"),
                    rs.getInt("rams_count"),
                    rs.getInt("power_supply_id"),
                    rs.getInt("cooler_id"),
                    rs.getInt("case_id"),
                    rs.getString("image_url"),
                    rs.getInt("stock_quantity")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return null;
        }
    }

    public boolean addComputer(Computer computer) {
        String insertComputerSQL = "INSERT INTO computers (name, description, price, processor_id, graphic_card_id, " +
                "motherboard_id, ram_id, rams_count, power_supply_id, cooler_id, case_id, image_url, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = con.prepareStatement(insertComputerSQL)) {
            pstmt.setString(1, computer.getName());
            pstmt.setString(2, computer.getDescription());
            pstmt.setBigDecimal(3, computer.getPrice());
            pstmt.setInt(4, computer.getProcessorId());
            pstmt.setInt(5, computer.getGraphicCardId());
            pstmt.setInt(6, computer.getMotherboardId());
            pstmt.setInt(7, computer.getRamId());
            pstmt.setInt(8, computer.getRamsCount());
            pstmt.setInt(9, computer.getPowerSupplyId());
            pstmt.setInt(10, computer.getCoolerId());
            pstmt.setInt(11, computer.getCaseId());
            pstmt.setString(12, computer.getImageUrl());
            pstmt.setInt(13, computer.getStockQuantity());
    
            return pstmt.executeUpdate() > 0;
    
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    public boolean updateComputer(Computer computer) {
        String updateComputer = "UPDATE computers SET name = ?, description = ?, price = ?, processor_id = ?, graphic_card_id = ?, motherboard_id = ?, ram_id = ?, rams_count = ?, power_supply_id = ?, cooler_id = ?, case_id = ?, image_url = ?, stock_quantity = ? WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(updateComputer)) {
            pstmt.setString(1, computer.getName());
            pstmt.setString(2, computer.getDescription());
            pstmt.setBigDecimal(3, computer.getPrice());
            pstmt.setInt(4, computer.getProcessorId());
            pstmt.setInt(5, computer.getGraphicCardId());
            pstmt.setInt(6, computer.getMotherboardId());
            pstmt.setInt(7, computer.getRamId());
            pstmt.setInt(8, computer.getRamsCount());
            pstmt.setInt(9, computer.getPowerSupplyId());
            pstmt.setInt(10, computer.getCoolerId());
            pstmt.setInt(11, computer.getCaseId());
            pstmt.setString(12, computer.getImageUrl());
            pstmt.setInt(13, computer.getStockQuantity());
            pstmt.setInt(14, computer.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    private void updateStockQuantities(Map<Integer, Integer> computerIdsAndQuantities) throws SQLException {
        String updateQuery = "UPDATE computers SET stock_quantity = stock_quantity - ? WHERE id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
            for (Map.Entry<Integer, Integer> entry : computerIdsAndQuantities.entrySet()) {
                int computerId = entry.getKey();
                int quantityToReduce = entry.getValue();
    
                pstmt.setInt(1, quantityToReduce);
                pstmt.setInt(2, computerId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }    

    private String checkStockAvailability(Map<Integer, Integer> computerIdsAndQuantities) {
        for (Map.Entry<Integer, Integer> entry : computerIdsAndQuantities.entrySet()) {
            int computerId = entry.getKey();
            int quantityRequested = entry.getValue();
    
            String stockCheckQuery = "SELECT stock_quantity, name FROM computers WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(stockCheckQuery)) {
                pstmt.setInt(1, computerId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int stockQuantity = rs.getInt("stock_quantity");
                    String computerName = rs.getString("name");
                    if (stockQuantity < quantityRequested) {
                        return "Out of stock: " + computerName; // недостаточно на складе
                    }
                } else {
                    return "Computer with ID " + computerId + " not found."; // Компьютер не найден
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return "Error during warehouse verification: " + e.getMessage();
            }
        }
        return null; // Все товары доступны
    }

    public boolean deleteComputer(Integer id) {
        String deleteComputer = "DELETE FROM computers WHERE id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteComputer)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }






    //==> SHOPPING CART CRUD
    // ==================================================================================================================================
    public List<ShoppingCartItem> getShoppingCartItemsByOwner(String owner) {
        String query = "SELECT * FROM shopping_cart WHERE owner = ?";
        List<ShoppingCartItem> items = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, owner);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ShoppingCartItem item = new ShoppingCartItem(
                    rs.getString("owner"),
                    rs.getInt("computer_id"),
                    rs.getInt("quantity")
                );
                items.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return items;
    }

    public ShoppingCartItem getShoppingCartItemById(String owner, Integer computer_id) {
        String query = "SELECT * FROM shopping_cart WHERE computer_id = ? AND owner = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, computer_id);
            pstmt.setString(2, owner);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ShoppingCartItem(
                    rs.getString("owner"),
                    rs.getInt("computer_id"),
                    rs.getInt("quantity")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        return null;
    }

    public boolean addShoppingCartItem(ShoppingCartItem item) {
        String insertComputerSQL = "INSERT INTO shopping_cart (owner, computer_id, quantity) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = con.prepareStatement(insertComputerSQL)) {
            pstmt.setString(1, item.getOwner());
            pstmt.setInt(2, item.getComputerId());
            pstmt.setInt(3, item.getQuantity());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean updateShoppingCartItem(ShoppingCartItem item) {
        String updateQuery = "UPDATE shopping_cart SET quantity = ? WHERE computer_id = ? AND owner = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
            pstmt.setInt(1, item.getQuantity());
            pstmt.setInt(2, item.getComputerId());
            pstmt.setString(3, item.getOwner());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean deleteShoppingCartItem(ShoppingCartItem item) {
        String deleteQuery = "DELETE FROM shopping_cart WHERE computer_id = ? AND owner = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, item.getComputerId());
            pstmt.setString(2, item.getOwner());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean clearShoppingCart(String username) {
        String deleteQuery = "DELETE FROM shopping_cart WHERE owner = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Возвращает true, если были удалены записи
        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Ошибка при очистке корзины: " + e.getMessage());
            return false;
        }
    }
    
    




    //==> ORDERS CRUD
    // ==================================================================================================================================
    public List<Order> getUserOrders(String username) {
        List<Order> orders = new ArrayList<>();
    
        String orderQuery = "SELECT * FROM orders WHERE customer = ?";
        
        try (PreparedStatement orderStmt = con.prepareStatement(orderQuery)) {
            orderStmt.setString(1, username);
            ResultSet orderRs = orderStmt.executeQuery();
    
            while (orderRs.next()) {
                Integer orderId = orderRs.getInt("id");
                List<OrderItem> orderItems = getOrderItems(orderId);

                orders.add(new Order(
                    orderId,
                    orderRs.getString("customer"),
                    orderRs.getTimestamp("order_date"),
                    new BigDecimal(orderRs.getDouble("total_amount")),
                    orderRs.getString("status"),
                    orderItems
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        
        return orders;
    }

    public OrderResult createOrder(String customerUsername, Map<Integer, Integer> computerIdsAndQuantities) {
        String checkStatus = checkStockAvailability(computerIdsAndQuantities);
        if (checkStatus != null) {
            return new OrderResult(false, checkStatus);
        }
    
        try {
            con.setAutoCommit(false); // Отключаем автоматическое подтверждение
            String orderInsertQuery = "INSERT INTO orders (customer, total_amount) VALUES (?, ?) RETURNING id";
            PreparedStatement orderStmt = con.prepareStatement(orderInsertQuery);
            orderStmt.setString(1, customerUsername);
            double totalAmount = calculateTotalAmount(computerIdsAndQuantities);
            orderStmt.setDouble(2, totalAmount);
            
            ResultSet generatedKeys = orderStmt.executeQuery();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);
                createOrderItems(orderId, computerIdsAndQuantities);
                updateStockQuantities(computerIdsAndQuantities);
                con.commit(); // Подтверждаем транзакцию
                return new OrderResult(true, "The order has been successfully created!");
            }
        } catch (SQLException e) {
            try {
                con.rollback(); // Откат транзакции в случае ошибки
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
                return new OrderResult(false, "Error on transaction rollback: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return new OrderResult(false, "Error when creating an order");
        } finally {
            try {
                con.setAutoCommit(true); // Включаем автоматическое подтверждение обратно
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new OrderResult(false, "Failed to create an order. Try again later");
    }

    private double calculateTotalAmount(Map<Integer, Integer> computerIdsAndQuantities) throws SQLException {
        double totalAmount = 0.0;
        for (Map.Entry<Integer, Integer> entry : computerIdsAndQuantities.entrySet()) {
            int computerId = entry.getKey();
            int quantityRequested = entry.getValue();

            String priceQuery = "SELECT price FROM computers WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(priceQuery)) {
                pstmt.setInt(1, computerId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    totalAmount += price * quantityRequested;
                }
            }
        }
        return totalAmount;
    }

    public boolean updateOrder(Order order) {
        String updateQuery = "UPDATE orders SET status = ?, total_amount = ?, order_date = ?, customer = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
            pstmt.setString(1, order.getStatus());
            pstmt.setBigDecimal(2, order.getTotalAmount());
            pstmt.setTimestamp(3, order.getOrderDate());
            pstmt.setString(4, order.getCustomer());
            pstmt.setInt(5, order.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean deleteOrder(Integer order_id) {
        String deleteQuery = "DELETE FROM orders WHERE id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, order_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }
    
    
    



    //==> ORDERS CRUD
    // ==================================================================================================================================
    private List<OrderItem> getOrderItems(Integer orderId) throws SQLException {
        List<OrderItem> orderItems = new ArrayList<>();
        
        String itemQuery = "SELECT * FROM order_items WHERE order_id = ?";
        
        try (PreparedStatement itemStmt = con.prepareStatement(itemQuery)) {
            itemStmt.setInt(1, orderId);
            ResultSet itemRs = itemStmt.executeQuery();
            
            while (itemRs.next()) {
                Integer itemId = itemRs.getInt("id");
                Integer computerId = itemRs.getInt("computer_id");
                Integer quantity = itemRs.getInt("quantity");
                BigDecimal price = new BigDecimal(itemRs.getDouble("price"));
    
                Computer pc = getComputerById(computerId);

                if (pc!= null) {
                    pc.setPrice(price);
                    orderItems.add(new OrderItem(
                        itemId,
                        orderId,
                        pc, 
                        quantity
                    ));
                }
            }
        }
        
        return orderItems;
    }

    public List<OrderItem> getAllOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        
        String itemQuery = "SELECT * FROM order_items";
        
        try (PreparedStatement itemStmt = con.prepareStatement(itemQuery)) {;
            ResultSet itemRs = itemStmt.executeQuery();
            
            while (itemRs.next()) {
                Integer orderId = itemRs.getInt("order_id");
                Integer itemId = itemRs.getInt("id");
                Integer computerId = itemRs.getInt("computer_id");
                Integer quantity = itemRs.getInt("quantity");
                BigDecimal price = new BigDecimal(itemRs.getDouble("price"));
    
                Computer pc = getComputerById(computerId);

                if (pc!= null) {
                    pc.setPrice(price.multiply(new BigDecimal(quantity)));
                    orderItems.add(new OrderItem(
                        itemId,
                        orderId,
                        pc, 
                        quantity
                    ));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
        }
        
        return orderItems;
    }

    public boolean updateOrderItem(OrderItem item) {
        String updateQuery = "UPDATE order_items SET order_id = ?, computer_id = ?, quantity = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(updateQuery)) {
            pstmt.setInt(1, item.getOrderId());
            pstmt.setInt(2, item.getComputer().getId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setInt(4, item.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    public boolean deleteOrderItem(Integer item_id) {
        String deleteQuery = "DELETE FROM order_items WHERE id = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, item_id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
            AlertHelper.showErrorAlert("Unknown Error. Try again");
            return false;
        }
    }

    private void createOrderItems(int orderId, Map<Integer, Integer> computerIdsAndQuantities) throws SQLException {
        String orderItemInsertQuery = "INSERT INTO order_items (order_id, computer_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(orderItemInsertQuery)) {
            for (Map.Entry<Integer, Integer> entry : computerIdsAndQuantities.entrySet()) {
                int computerId = entry.getKey();
                int quantityRequested = entry.getValue();
    
                Computer computer = getComputerById(computerId);

                if (computer != null) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, computerId);
                    pstmt.setInt(3, quantityRequested);
                    pstmt.setBigDecimal(4, computer.getPrice());
                    pstmt.addBatch();
                } else {
                    throw new SQLException("Computer with ID " + computerId + " not found.");
                }
            }
            pstmt.executeBatch();
        }
    }
}   
