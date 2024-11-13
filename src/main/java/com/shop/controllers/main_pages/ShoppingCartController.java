package com.shop.controllers.main_pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.controllers.SharedData;
import com.shop.controllers.main_pages.childrens.ShoppingCartItemController;
import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.ShoppingCartItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShoppingCartController implements Initializable {
    @FXML
    AnchorPane root;
    @FXML 
    VBox itemsList;
    @FXML
    ScrollPane scrollPane;

    private ObservableList<Computer> computersList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        computersList.addAll(DbConnection.getDatabaseConnection().getAllComputers());

        for (Computer computer : computersList) {
            try {
                ShoppingCartItem cartItem = DbConnection.getDatabaseConnection().getShoppingCartItemById(
                    SharedData.getAuthenticatedUser().getUsername(),
                    computer.getId()
                );

                if (cartItem != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/main_pages/childrens/shoppingCartItem.fxml"));
                    Parent computerItem = loader.load();
                    ShoppingCartItemController controller = loader.getController();
                    controller.setProductData(computer, cartItem.getQuantity());
                    itemsList.getChildren().add(computerItem);
                }
            } catch (IOException ex) {
                Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }

        VBox.setVgrow(itemsList, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
    }
}
