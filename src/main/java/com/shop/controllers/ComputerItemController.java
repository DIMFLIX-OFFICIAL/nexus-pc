package com.shop.controllers;

import java.math.BigDecimal;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

    public void setProductData(String imageUrl, String name, String description, BigDecimal price) {
        computerImage.setImage(new Image(imageUrl));
        computerName.setText(name);
        computerDescription.setText(description);
        computerPrice.setText(String.format("%s₽", price));
        HBox.setHgrow(emptySpace, Priority.ALWAYS);
    }

    @FXML
    private void addToShoppingCart() {
        System.out.println("addToShoppingCart event");
    }
}
