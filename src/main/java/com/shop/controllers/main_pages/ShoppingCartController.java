package com.shop.controllers.main_pages;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.controllers.SharedData;
import com.shop.controllers.main_pages.childrens.ShoppingCartItemController;
import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.OrderResult;
import com.shop.database.models.ShoppingCartItem;
import com.shop.helper.AlertHelper;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ShoppingCartController implements Initializable {
    @FXML
    AnchorPane root;
    @FXML 
    VBox itemsList;
    @FXML
    ScrollPane scrollPane;
    @FXML
    HBox controlsBar;
    @FXML
    HBox leftControlsBar;
    @FXML
    HBox rightControlsBar;
    @FXML
    Button makeOrderBtn;
    @FXML
    Label totalCost;

    private List<ShoppingCartItemController> itemControllers = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (Computer computer : FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllComputers())) {
            try {
                ShoppingCartItem cartItem = DbConnection.getDatabaseConnection().getShoppingCartItemById(
                    SharedData.getAuthenticatedUser().getUsername(),
                    computer.getId()
                );

                if (cartItem != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/main_pages/childrens/shoppingCartItem.fxml"));
                    Parent computerItem = loader.load();
                    ShoppingCartItemController controller = loader.getController();
                    controller.setProductData(computer, cartItem.getQuantity(), this);
                    itemsList.getChildren().add(computerItem);
                    itemControllers.add(controller);
                }
            } catch (IOException ex) {
                Logger.getLogger(ShoppingCartController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }

        if (itemControllers.size() == 0) {
            Label label = new Label();
            label.setFont(Font.font("Arial", 30));
            label.setText("Your cart is empty");
            itemsList.getChildren().add(label);
            scrollPane.setFitToHeight(true);
            controlsBar.setVisible(false);
        }

        VBox.setVgrow(itemsList, Priority.ALWAYS);
        HBox.setHgrow(leftControlsBar, Priority.ALWAYS);
        HBox.setHgrow(rightControlsBar, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        updateTotalCost();
    }

    public void updateTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (ShoppingCartItemController itemController : itemControllers) {
            total = total.add(itemController.getPrice());
        }
        totalCost.setText(String.format("Total cost: %s₽", total));
    }

    @FXML
    private void makeOrder() {
        Map<Integer, Integer> computerIdsAndQuantities = new HashMap<>();
        for (ShoppingCartItemController itemController : itemControllers) {
            computerIdsAndQuantities.put(
                itemController.getComputer().getId(), 
                itemController.getQuantity()
            );
        }

        OrderResult result = DbConnection.getDatabaseConnection().createOrder(
            SharedData.getAuthenticatedUser().getUsername(), 
            computerIdsAndQuantities
        );

        if (result.isSuccess()) {
            AlertHelper.showSuccessAlert("Order created successfully!");
            DbConnection.getDatabaseConnection().clearShoppingCart(
                SharedData.getAuthenticatedUser().getUsername()
            );
            SharedData.getMainController().loadShoppingCartView(null);
        } else {
            AlertHelper.showErrorAlert(result.getMessage());
        }
    }
}
