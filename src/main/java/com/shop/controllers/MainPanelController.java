package com.shop.controllers;

import com.shop.database.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class MainPanelController implements Initializable {
    User user;

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox pagesWindow;

    @FXML
    private Button CatalogPageButton;

    @FXML
    private Button ShoppingCartPageButton;

    @FXML
    private Button OrdersPageButton;

    @FXML
    private SplitMenuButton AdminPagesButton;

    @FXML
    private Label windowName;

    @FXML
    private HBox topPanel;

    private double mouseX;
    private double mouseY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        topPanel.setOnMousePressed(event -> {
            mouseX = event.getScreenX() - ((Stage) topPanel.getScene().getWindow()).getX();
            mouseY = event.getScreenY() - ((Stage) topPanel.getScene().getWindow()).getY();
        });

        topPanel.setOnMouseDragged(event -> {
            Stage stage = (Stage) topPanel.getScene().getWindow();
            stage.setX(event.getScreenX() - mouseX);
            stage.setY(event.getScreenY() - mouseY);
        });

        MenuItem usersAdminTable = new MenuItem("Users");
        MenuItem processorsAdminTable = new MenuItem("Processors");
        MenuItem graphicCardsAdminTable = new MenuItem("Graphic Cards");
        MenuItem motherboardsAdminTable = new MenuItem("Motherboards");
        MenuItem powerSuppliesAdminTable = new MenuItem("Power Supply");
        MenuItem ramsAdminTable = new MenuItem("RAMs");
        MenuItem coolersAdminTable = new MenuItem("Coolers");
        MenuItem casesAdminTable = new MenuItem("Cases");
        MenuItem computersAdminTable = new MenuItem("Computers");
        MenuItem ordersAdminTable = new MenuItem("Orders");
        MenuItem orderItemsAdminTable = new MenuItem("Order Items");


        usersAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/users", "Users Table");
        });
        processorsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/processors", "Processors Table");
        });
        graphicCardsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/graphicCards", "Grapic Cards Table");
        });
        motherboardsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/motherboards", "Motherboards Table");
        });
        powerSuppliesAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/powerSupplies", "Power Supplies Table");
        });
        ramsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/rams", "RAMs Table");
        });
        coolersAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/coolers", "Coolers Table");
        });
        casesAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/cases", "Caces Table");
        });
        computersAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/computers", "Computers Table");
        });
        ordersAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/orders", "Orders Table");
        });
        orderItemsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/orderItems", "Order Items Table");
        });
        
        AdminPagesButton.getItems().addAll(
            usersAdminTable,processorsAdminTable, graphicCardsAdminTable, 
            motherboardsAdminTable, powerSuppliesAdminTable, ramsAdminTable,
            coolersAdminTable, casesAdminTable, computersAdminTable,
            ordersAdminTable, orderItemsAdminTable
        );

        if (!(SharedData.getAuthenticatedUser().getRole().equals("admin"))) {
            AdminPagesButton.setVisible(false);
        }

        SharedData.setMainController(this);
        loadCatalogView(null); // start page
        
    }

    @FXML
    private void clear() {
        pagesWindow.getChildren().clear();
    }

    @FXML
    public void loadFXML(String fileName, String PageName) {
        Parent parent;
        try {
            parent = FXMLLoader.load(getClass().getResource(fileName + ".fxml"));
            HBox.setHgrow(parent, Priority.ALWAYS);
            clear();
            pagesWindow.getChildren().add(parent);
            windowName.setText(PageName);
        } catch (IOException ex) {
            Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void logout() throws IOException {
        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("/com/shop/auth/LoginView.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("User Login");
        stage.show();
    }

    @FXML
    private void loadCatalogView(ActionEvent e) {
        loadFXML("/com/shop/main_pages/CatalogView", "Catalog");
    }

    @FXML
    public void loadShoppingCartView(ActionEvent e) {
        loadFXML("/com/shop/main_pages/ShoppingCartView", "Shopping Cart");
    }

    @FXML
    private void loadMyOrdersView(ActionEvent e) {
        loadFXML("/com/shop/main_pages/MyOrdersView", "My Orders");
    }

    public void loadComputerInfoView() {
        loadFXML("/com/shop/main_pages/ComputerInfoView", "Computer Information");
    }

    @FXML
    public void loadFiltersView() {
        loadFXML("/com/shop/main_pages/FiltersView", "Filters");
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) topPanel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimizeButton() {
        Stage stage = (Stage) topPanel.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleMaximizeButton() {
        Stage stage = (Stage) topPanel.getScene().getWindow();
        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        } else {
            stage.setMaximized(false); 
        }
    }
}
