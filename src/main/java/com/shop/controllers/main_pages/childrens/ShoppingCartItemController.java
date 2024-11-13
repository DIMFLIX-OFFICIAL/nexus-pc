package com.shop.controllers.main_pages.childrens;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.controllers.MainPanelController;
import com.shop.controllers.SharedData;
import com.shop.controllers.main_pages.ShoppingCartController;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class ShoppingCartItemController {
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
    private Button removeFromCartBtn;
    @FXML
    private Spinner<Integer> computersCount;

    public void setProductData(Computer pc, Integer quantity, ShoppingCartController mainController) {
        computer = pc;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
            .IntegerSpinnerValueFactory(1, 100, quantity);

        new Thread(() -> {
            try {
                Image image = new Image(pc.getImageUrl());
                if (image.isError()) {
                    System.out.println("Ошибка загрузки изображения: " + image.getException());
                    AlertHelper.showErrorAlert("Error loading images");
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

        computerName.setText(pc.getName());
        computerDescription.setText(pc.getDescription());

        computer.setPrice(computer.getPrice().multiply(new BigDecimal(quantity)));
        computerPrice.setText(String.format("%s₽", computer.getPrice()));

        computersCount.setValueFactory(valueFactory);
        computersCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean update_status = DbConnection.getDatabaseConnection().updateShoppingCartItem(
                new ShoppingCartItem(
                    SharedData.getAuthenticatedUser().getUsername(), 
                    computer.getId(), 
                    newValue
                )
            );

            if (update_status == true) {
                computer.setPrice(computer.getPrice()
                    .divide(new BigDecimal(oldValue))
                    .multiply(new BigDecimal(newValue)));
                computerPrice.setText(String.format("%s₽", computer.getPrice()));
                
                if (mainController != null) {
                    mainController.updateTotalCost();
                }
            } else {
                AlertHelper.showErrorAlert("Failed to update item quantity in cart...");
            }
        });

        HBox.setHgrow(emptySpace, Priority.ALWAYS);
    }

    @FXML
    private void showComputerInfo() {
        SharedData.setSelectedComputer(computer);
        SharedData.getMainController().loadComputerInfoView();
    }

    @FXML
    private void removeFromShoppingCart() {
        boolean deleted = DbConnection.getDatabaseConnection().deleteShoppingCartItem(
            new ShoppingCartItem(SharedData.getAuthenticatedUser().getUsername(), computer.getId(), 1)
        );

        if (deleted) {
            SharedData.getMainController().loadShoppingCartView(null);
        } else {
            AlertHelper.showErrorAlert("Failed to remove item from cart...");
        }
    }

    public BigDecimal getPrice() {
        return computer.getPrice();
    }
}
