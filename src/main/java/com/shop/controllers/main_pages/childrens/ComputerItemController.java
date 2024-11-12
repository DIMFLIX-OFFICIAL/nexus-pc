package com.shop.controllers.main_pages.childrens;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.controllers.MainPanelController;
import com.shop.helper.AlertHelper;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ComputerItemController {
    @FXML
    private AnchorPane root;
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

    public void setProductData(String imageUrl, String name, String description, BigDecimal price) {
        new Thread(() -> {
            try {
                Image image = new Image(imageUrl);
                if (image.isError()) {
                    System.out.println("Ошибка загрузки изображения: " + image.getException());
                    AlertHelper.showErrorAlert("Error loading images");
                } else {
                    Platform.runLater(() -> {
                        String frm = String.format("-fx-image: url('%s');", imageUrl);
                        computerImage.setStyle(frm);
                    });
                }
            } catch (Exception e) {
                Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, e);
                AlertHelper.showErrorAlert("Unknown Error. Try again");
            }
        }).start();
        computerName.setText(name);
        computerDescription.setText(description);
        computerPrice.setText(String.format("%s₽", price));
        HBox.setHgrow(emptySpace, Priority.ALWAYS);
    }

    @FXML
    private void addToShoppingCart() {
        if (addToCartBtn.getText().equals("Add to cart")) { 
            addToCartBtn.setText("Remove from cart");
            addToCartBtn.setStyle("-fx-background-color: #eba0ac;");
            System.out.println("addToShoppingCart event");
        } else {
            addToCartBtn.setText("Add to cart");
            addToCartBtn.setStyle("-fx-background-color: #b4befe;");
            System.out.println("removeFromShoppingCart event");
        }
    }
}
