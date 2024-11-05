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
    private AreaChart<?, ?> chartPurchase;

    @FXML
    private AreaChart<?, ?> chartSale;

    @FXML
    private LineChart<?, ?> chartReceipt;

    @FXML
    private Button CatalogPageButton;

    @FXML
    private Button ShoppingCartPageButton;

    @FXML
    private Button OrdersPageButton;

    @FXML
    private SplitMenuButton AdminPagesButton;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        MenuItem usersAdminTable = new MenuItem("Users");
        MenuItem processorsAdminTable = new MenuItem("Processors");
        MenuItem graphicCardsAdminTable = new MenuItem("Graphic Cards");

        AdminPagesButton.getItems().addAll(usersAdminTable,processorsAdminTable, graphicCardsAdminTable);
        usersAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/users");
        });
        processorsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/processors");
        });
        graphicCardsAdminTable.setOnAction((e)-> {
            loadFXML("/com/shop/admin_pages/graphicCards");
        });
        

        loadCatalogView(null); // start page
    }

    public void setAuthUser(User user) {
        this.user = user;
        System.out.println(this.user.getRole());
        if (this.user == null || !this.user.getRole().equals("admin")) {
            AdminPagesButton.setVisible(false);
        }
    }

    @FXML
    private void clear() {
        pagesWindow.getChildren().clear();
    }

    @FXML
    private void loadFXML(String fileName) {
        Parent parent;
        try {
            parent = FXMLLoader.load(getClass().getResource(fileName + ".fxml"));
            HBox.setHgrow(parent, Priority.ALWAYS);
            clear();
            pagesWindow.getChildren().add(parent);
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
        loadFXML("/com/shop/main_pages/CatalogView");
    }

    @FXML
    private void loadShoppingCartView(ActionEvent e) {
        loadFXML("/com/shop/main_pages/ShoppingCartView");
    }

    @FXML
    private void loadMyOrdersView(ActionEvent e) {
        loadFXML("/com/shop/main_pages/MyOrdersView");
    }
}
