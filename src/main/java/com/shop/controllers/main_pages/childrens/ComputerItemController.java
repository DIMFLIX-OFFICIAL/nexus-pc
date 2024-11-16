package com.shop.controllers.main_pages.childrens;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.controllers.MainPanelController;
import com.shop.controllers.SharedData;
import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.ShoppingCartItem;
import com.shop.helper.AlertHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ComputerItemController {
    private Computer computer;

    @FXML
    private HBox emptySpace;
    @FXML
    private ImageView computerImage;
    @FXML
    private Label computerName;
    @FXML
    private Label computerDescription;
    @FXML
    private Label computerPrice;
    @FXML
    private Button addToCartBtn;

    public void setProductData(Computer pc) {
        computer = pc;

        new Thread(() -> {
            try {
                Image image = new Image(pc.getImageUrl());
                if (image.isError()) {
                    Platform.runLater(() -> {
                        computerImage.setImage(new Image(getClass().getResource("/com/shop/pc.png").toString()));
                    });
                } else {
                    Platform.runLater(() -> {
                        String frm = String.format("-fx-image: url('%s');", pc.getImageUrl());
                        computerImage.setStyle(frm);
                    });
                }
            } catch (Exception e) {
                Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, e);
                AlertHelper.showErrorAlert("Unknown Error. Try again");
            }
        }).start();

        ShoppingCartItem item = DbConnection.getDatabaseConnection().getShoppingCartItemById(
            SharedData.getAuthenticatedUser().getUsername(),
            computer.getId()
        );

        if (item != null) {
            setStateBtn(false);
        } else {
            setStateBtn(true);
        }

        computerName.setText(pc.getName());
        computerDescription.setText(pc.getDescription());
        computerPrice.setText(String.format("%s₽", pc.getPrice()));
        HBox.setHgrow(emptySpace, Priority.ALWAYS);
    }

    public void setStateBtn(Boolean state) {
        // If state is True - btn has text "Add to cart"
        // Else - btn has text "Remove from cart"

        if (state) {
            addToCartBtn.setText("Add to cart");
            addToCartBtn.setStyle("-fx-background-color: #b4befe; -fx-cursor: hand;");
        } else {
            addToCartBtn.setText("Remove from cart");
            addToCartBtn.setStyle("-fx-background-color: #eba0ac; -fx-cursor: hand;");
        }
    }

    @FXML
    private void showComputerInfo() {
        SharedData.setSelectedComputer(computer);
        SharedData.getMainController().loadComputerInfoView();
    }

    @FXML
    private void addToShoppingCart() {
        if (addToCartBtn.getText().equals("Add to cart")) { 
            boolean added = DbConnection.getDatabaseConnection().addShoppingCartItem(
                new ShoppingCartItem(SharedData.getAuthenticatedUser().getUsername(), computer.getId(), 1)
            );

            if (added) {
                setStateBtn(false);
            } else {
                AlertHelper.showErrorAlert("Failed to add to cart...");
            }
        } else {    
            boolean deleted = DbConnection.getDatabaseConnection().deleteShoppingCartItem(
                new ShoppingCartItem(SharedData.getAuthenticatedUser().getUsername(), computer.getId(), 1)
            );

            if (deleted) {
                setStateBtn(true);
            } else {
                AlertHelper.showErrorAlert("Failed to remove item from cart...");
            }

        }
    }
}
