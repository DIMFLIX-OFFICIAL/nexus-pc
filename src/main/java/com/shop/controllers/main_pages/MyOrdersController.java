package com.shop.controllers.main_pages;

import java.awt.Desktop;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.GroupLayout.Alignment;

import com.shop.controllers.MainPanelController;
import com.shop.controllers.SharedData;
import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.Order;
import com.shop.database.models.OrderItem;
import com.shop.helper.AlertHelper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class MyOrdersController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private TreeView<String> tree;

    private List<Order> ordersList;
    private static final String URL_REGEX = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/.*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.ordersList = DbConnection.getDatabaseConnection().getUserOrders(
            SharedData.getAuthenticatedUser().getUsername()
        );

        setInfoInTreeView();
    }

    private void setInfoInTreeView() {

        if (ordersList == null || ordersList.size() == 0) {
            root.getChildren().remove(tree);
            Label label = new Label();
            label.setFont(Font.font("Arial", 30));
            label.setStyle("-fx-text-fill: #ffffff;");
            label.setText("You have no orders");
            label.setAlignment(Pos.CENTER);

            AnchorPane.setTopAnchor(label, 0.0);
            AnchorPane.setBottomAnchor(label, 0.0);
            AnchorPane.setLeftAnchor(label, 0.0);
            AnchorPane.setRightAnchor(label, 0.0);

            root.getChildren().add(label);
            
            
        }



        TreeItem<String> rootItem = new TreeItem<>("Your orders");
        rootItem.setExpanded(true);
        Collections.reverse(ordersList);

        for (Order order : ordersList) {
            TreeItem<String> item = new TreeItem<>(String.format("Order from %s in the amount of %s₽", sdf.format(order.getOrderDate()), order.getTotalAmount()));
            item.getChildren().add(new TreeItem<>(String.format("Time: %s", sdf.format(order.getOrderDate()))));
            item.getChildren().add(new TreeItem<>(String.format("Total Amount: %s₽", order.getTotalAmount())));
            item.getChildren().add(new TreeItem<>(String.format("Status: %s", order.getStatus())));
            item.getChildren().add(new TreeItem<>(String.format("Comment: %s", order.getComment())));

            TreeItem<String> selected_computers = new TreeItem<>("Selected computers");
            for (OrderItem order_item : order.getItems()) {
                Computer pc = order_item.getComputer();

                TreeItem<String> pc_item = new TreeItem<>(pc.getName());
                pc_item.getChildren().add(new TreeItem<>(String.format("Quantity: %s", order_item.getQuantity())));
                pc_item.getChildren().add(new TreeItem<>(String.format("Cost per one: %s", pc.getPrice())));
                pc_item.getChildren().add(new TreeItem<>(String.format("Total cost: %s", pc.getPrice().multiply(
                    new BigDecimal(order_item.getQuantity())
                ))));

                TreeItem<String> viewInfoItem = new TreeItem<>(String.format("View information about computer number %s", pc.getId()));
                pc_item.getChildren().add(viewInfoItem);
                

                pc_item.setExpanded(true);
                selected_computers.getChildren().add(pc_item);
            }
            item.getChildren().add(selected_computers);


            item.setExpanded(true);
            rootItem.getChildren().add(item);
        }

        tree.setRoot(rootItem);

        tree.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (getTreeItem().getParent() == null) {
                        setFont(Font.font("Arial", 20));
                    } else if (getTreeItem().getParent() != null && getTreeItem().getParent().getParent() == null) {
                        setFont(Font.font("Arial", 18));
                    } else { 
                        setFont(Font.font("Arial", 16));
                    }

                    if (item.equals("Status: Pending")) {
                        setStyle("-fx-background-color: #fab387; -fx-font-weight: bold;");
                    }

                    if (item.equals("Status: Delivered")) {
                        setStyle("-fx-background-color: #a6e3a1; -fx-font-weight: bold;");
                    }

                    if (item.startsWith("Order from")) {
                        setStyle("-fx-text-fill: #b4befe; -fx-font-weight: bold;");
                    }

                    if (item.startsWith("View information about computer number")) {
                        setStyle("-fx-text-fill: #b4befe; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedItem = tree.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String item = selectedItem.getValue();
                    
                    if (item.startsWith("View information about computer number")) {
                        Integer computer_id = Integer.parseInt(item.replace("View information about computer number", "").strip());
                        Computer computer = DbConnection.getDatabaseConnection().getComputerById(computer_id);

                        if (computer != null) {
                            SharedData.setSelectedComputer(computer);
                            SharedData.getMainController().loadComputerInfoView();
                        } else {
                            AlertHelper.showErrorAlert("Computer not found.");
                        }
                    }
                }
            }
        });
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
