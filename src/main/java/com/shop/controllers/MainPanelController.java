package com.shop.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainPanelController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private HBox pagesWindow;

    private List<Button> menus;

    @FXML
    private AreaChart<?, ?> chartPurchase;

    @FXML
    private AreaChart<?, ?> chartSale;

    @FXML
    private LineChart<?, ?> chartReceipt;

    @FXML
    private Button buttonCatalog; // Example button from FXML

    @FXML
    private Button buttonShoppingCart; // Another example button

    @FXML
    private Button buttonMyOrders; // Another example button

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menus = new ArrayList<>();
        menus.add(buttonCatalog);
        menus.add(buttonShoppingCart);
        menus.add(buttonMyOrders);

        loadFXML("CatalogView"); // start page
    }

    @FXML
    private void clear() {
        pagesWindow.getChildren().clear();
    }

    @FXML
    private void loadFXML(String fileName) {
        Parent parent;
        try {
            parent = FXMLLoader.load(getClass().getResource("/com/shop/main_pages/" + fileName + ".fxml"));
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
        loadFXML("CatalogView");
    }

    @FXML
    private void loadShoppingCartView(ActionEvent e) {
        loadFXML("ShoppingCartView");
    }

    @FXML
    private void loadMyOrdersView(ActionEvent e) {
        loadFXML("MyOrdersView");
    }
}
