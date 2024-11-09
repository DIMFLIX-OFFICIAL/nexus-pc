package com.shop.controllers.main_pages;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.database.models.Computer;
import com.shop.controllers.ComputerItemController;
import com.shop.controllers.MainPanelController;
import com.shop.database.DbConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CatalogsController implements Initializable {
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/computerItem.fxml"));
                Parent root = loader.load();
                ComputerItemController controller = loader.getController();
                controller.setProductData(computer.getImageUrl(), computer.getName(), computer.getDescription(), computer.getPrice());
                itemsList.getChildren().add(root);
            } catch (IOException ex) {
                Logger.getLogger(MainPanelController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }

        VBox.setVgrow(itemsList, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
    }
}
