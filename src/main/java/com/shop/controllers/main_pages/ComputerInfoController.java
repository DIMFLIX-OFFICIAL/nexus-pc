package com.shop.controllers.main_pages;

import com.shop.database.models.Computer;
import com.shop.database.models.Cooler;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Motherboard;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.Processor;
import com.shop.database.models.RAM;
import com.shop.database.models.ShoppingCartItem;
import com.shop.helper.AlertHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.Desktop;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.shop.controllers.MainPanelController;
import com.shop.controllers.SharedData;
import com.shop.database.DbConnection;
import com.shop.database.models.Case;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ComputerInfoController implements Initializable {
    private Computer computer;
    private Processor processor;
    private GraphicCard graphicCard;
    private Motherboard motherboard;
    private RAM ram;
    private PowerSupply powerSupply;
    private Cooler cooler;
    private Case computerCase;

    private static final String URL_REGEX = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    @FXML
    private TreeView<String> tree;
    @FXML
    private Label computerName;
    @FXML
    private Label computerDescription;
    @FXML
    private Label computerPrice;
    @FXML
    private ImageView computerImage;
    @FXML
    private Button addToCartBtn;
    @FXML
    private HBox emptySpace;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HBox.setHgrow(emptySpace, Priority.ALWAYS);

        computer = SharedData.getSelectedComputer();
        ShoppingCartItem item = DbConnection.getDatabaseConnection().getShoppingCartItemById(
            SharedData.getAuthenticatedUser().getUsername(),
            computer.getId()
        );

        if (item != null) {
            setStateBtn(false);
        } else {
            setStateBtn(true);
        }

        computerName.setText(computer.getName());
        computerDescription.setText(computer.getDescription());
        computerPrice.setText(String.format("%s₽", computer.getPrice()));
        new Thread(() -> {
            try {
                Image image = new Image(computer.getImageUrl());
                if (image.isError()) {
                    Platform.runLater(() -> {
                        computerImage.setImage(new Image(getClass().getResource("/com/shop/pc.png").toString()));
                    });
                } else {
                    Platform.runLater(() -> {
                        String frm = String.format("-fx-image: url('%s');", computer.getImageUrl());
                        computerImage.setStyle(frm);
                    });
                }
            } catch (Exception e) {
                Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, e);
                AlertHelper.showErrorAlert("Unknown Error. Try again");
            }
        }).start();
        this.processor = DbConnection.getDatabaseConnection().getProcessorById(computer.getProcessorId());
        this.graphicCard = DbConnection.getDatabaseConnection().getGraphicCardById(computer.getGraphicCardId());
        this.motherboard = DbConnection.getDatabaseConnection().getMotherboardById(computer.getMotherboardId());
        this.ram = DbConnection.getDatabaseConnection().getRAMById(computer.getRamId());
        this.powerSupply = DbConnection.getDatabaseConnection().getPowerSupplyById(computer.getPowerSupplyId());
        this.cooler = DbConnection.getDatabaseConnection().getCoolerById(computer.getCoolerId());
        this.computerCase = DbConnection.getDatabaseConnection().getCaseById(computer.getCaseId());
        setInfoInTreeView();
    }

    private void setInfoInTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Computer components");
        rootItem.setExpanded(true);

        TreeItem<String> processorItem = new TreeItem<>("Processor");
        processorItem.getChildren().add(new TreeItem<>(String.format("Name: %s", processor != null ? processor.getName() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", processor != null ? processor.getBrand() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Cores: %s", processor != null ? processor.getCores() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Threads: %s", processor != null ? processor.getThreads() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Base Clock: %s", processor != null ? processor.getBaseClock() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Boost Clock: %s", processor != null ? processor.getBoostClock() : "undefined")));
        processorItem.getChildren().add(new TreeItem<>(String.format("Link: %s", processor != null ? processor.getLink() : "undefined")));
        processorItem.setExpanded(true);
        rootItem.getChildren().add(processorItem);

        TreeItem<String> graphicCardItem = new TreeItem<>("Graphic card");
        graphicCardItem.getChildren().add(new TreeItem<>(String.format("Name: %s", graphicCard != null ? graphicCard.getName() : "undefined")));
        graphicCardItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", graphicCard != null ? graphicCard.getBrand() : "undefined")));
        graphicCardItem.getChildren().add(new TreeItem<>(String.format("MemorySize: %s", graphicCard != null ? graphicCard.getMemorySize() : "undefined")));
        graphicCardItem.getChildren().add(new TreeItem<>(String.format("MemoryType: %s", graphicCard != null ? graphicCard.getMemoryType() : "undefined")));
        graphicCardItem.getChildren().add(new TreeItem<>(String.format("Link: %s", graphicCard != null ? graphicCard.getLink() : "undefined")));
        graphicCardItem.setExpanded(true);
        rootItem.getChildren().add(graphicCardItem);

        TreeItem<String> powerSupplyItem = new TreeItem<>("Power Supply");
        powerSupplyItem.getChildren().add(new TreeItem<>(String.format("Name: %s", powerSupply != null ? powerSupply.getName() : "undefined")));
        powerSupplyItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", powerSupply != null ? powerSupply.getBrand() : "undefined")));
        powerSupplyItem.getChildren().add(new TreeItem<>(String.format("Wattage: %s", powerSupply != null ? powerSupply.getWattage() : "undefined")));
        powerSupplyItem.getChildren().add(new TreeItem<>(String.format("Efficiency Rating: %s", powerSupply != null ? powerSupply.getEfficiencyRating() : "undefined")));
        powerSupplyItem.getChildren().add(new TreeItem<>(String.format("Link: %s", powerSupply != null ? powerSupply.getLink() : "undefined")));
        powerSupplyItem.setExpanded(true);
        rootItem.getChildren().add(powerSupplyItem);

        TreeItem<String> motherboardItem = new TreeItem<>("Motherboard");
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Name: %s", motherboard != null ? motherboard.getName() : "undefined")));
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", motherboard != null ? motherboard.getBrand() : "undefined")));
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Socket Type: %s", motherboard != null ? motherboard.getSocketType() : "undefined")));
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Form Factor: %s", motherboard != null ? motherboard.getFormFactor() : "undefined")));
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Max Memory: %s", motherboard != null ? motherboard.getMaxMemory() : "undefined")));
        motherboardItem.getChildren().add(new TreeItem<>(String.format("Link: %s", motherboard != null ? motherboard.getLink() : "undefined")));
        motherboardItem.setExpanded(true);
        rootItem.getChildren().add(motherboardItem);

        TreeItem<String> ramItem = new TreeItem<>("RAM");
        ramItem.getChildren().add(new TreeItem<>(String.format("Name: %s", ram != null ? ram.getName() : "undefined")));
        ramItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", ram != null ? ram.getBrand() : "undefined")));
        ramItem.getChildren().add(new TreeItem<>(String.format("Capacity (GB): %s", ram != null ? ram.getCapacity() : "undefined")));
        ramItem.getChildren().add(new TreeItem<>(String.format("Speed (MHz): %s", ram != null ? ram.getSpeed() : "undefined")));
        ramItem.getChildren().add(new TreeItem<>(String.format("Link: %s", ram != null ? ram.getLink() : "undefined")));
        ramItem.setExpanded(true);
        rootItem.getChildren().add(ramItem);


        TreeItem<String> coolerItem = new TreeItem<>("Cooler");
        coolerItem.getChildren().add(new TreeItem<>(String.format("Name: %s", cooler != null ? cooler.getName() : "undefined")));
        coolerItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", cooler != null ? cooler.getBrand() : "undefined")));
        coolerItem.getChildren().add(new TreeItem<>(String.format("Type: %s", cooler != null ? cooler.getType() : "undefined")));
        coolerItem.getChildren().add(new TreeItem<>(String.format("Cooling Capacity: %s", cooler != null ? cooler.getCoolingCapacity() : "undefined")));
        coolerItem.getChildren().add(new TreeItem<>(String.format("Link: %s", cooler != null ? cooler.getLink() : "undefined")));
        coolerItem.setExpanded(true);
        rootItem.getChildren().add(coolerItem);

        TreeItem<String> caseItem = new TreeItem<>("Case");
        caseItem.getChildren().add(new TreeItem<>(String.format("Name: %s", computerCase != null ? computerCase.getName() : "undefined")));
        caseItem.getChildren().add(new TreeItem<>(String.format("Brand: %s", computerCase != null ? computerCase.getBrand() : "undefined")));
        caseItem.getChildren().add(new TreeItem<>(String.format("Form Factor: %s", computerCase != null ? computerCase.getFormFactor() : "undefined")));
        caseItem.getChildren().add(new TreeItem<>(String.format("Color: %s", computerCase != null ? computerCase.getColor() : "undefined")));
        caseItem.getChildren().add(new TreeItem<>(String.format("Link: %s", computerCase != null ? computerCase.getLink() : "undefined")));
        caseItem.setExpanded(true);
        rootItem.getChildren().add(caseItem);

        tree.setRoot(rootItem);

        tree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    if (getTreeItem().getParent() == null) {
                        setFont(Font.font("Arial", 20));
                        setTextFill(Color.BLACK);
                    } else if (getTreeItem().getParent() != null && getTreeItem().getParent().getParent() == null) {
                        setFont(Font.font("Arial", 18));
                    } else { 
                        setFont(Font.font("Arial", 16));
                    }

                    if (item.startsWith("Link: ")) {
                        setStyle("-fx-cursor: hand;");
                    }
                }
            }
        });

        tree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String item = selectedItem.getValue();
                    
                    if (item.startsWith("Link: ")) {
                        openWebpage(item.replace("Link: ", "").strip());
                    }
                }
            }
        });
    }

    private void setStateBtn(Boolean state) {
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

    private void openWebpage(String urlString) {
        if (!isValidUrl(urlString)) {
            AlertHelper.showErrorAlert("The link is wrong. Please notify the administrator.");
            return;
        }
    
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(new URI(urlString));
            } catch (IOException | URISyntaxException e) {
                Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, e);
                AlertHelper.showErrorAlert("Unknown error. Try again.");
            }
        }).start();
    }
    
    private boolean isValidUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }
}
