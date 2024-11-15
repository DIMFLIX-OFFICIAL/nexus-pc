package com.shop.controllers.main_pages;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shop.database.models.Computer;
import com.shop.controllers.SharedData;
import com.shop.controllers.main_pages.childrens.ComputerItemController;
import com.shop.database.DbConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CatalogsController implements Initializable {
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
        Integer count = 0;

        for (Computer computer : computersList) {
            if (SharedData.getProcessorFilter() != null && SharedData.getProcessorFilter().getId() != computer.getProcessorId()) {
                continue;
            }

            if (SharedData.getGraphicCardFilter() != null && SharedData.getGraphicCardFilter().getId() != computer.getGraphicCardId()) {
                continue;
            }

            if (SharedData.getMotherboardFilter() != null && SharedData.getMotherboardFilter().getId() != computer.getMotherboardId()) {
                continue;
            }

            if (SharedData.getPowerSupplyFilter() != null && SharedData.getPowerSupplyFilter().getId() != computer.getPowerSupplyId()) {
                continue;
            }

            if (SharedData.getRamFilter() != null && SharedData.getRamFilter().getId() != computer.getRamId()) {
                continue;
            }

            if (SharedData.getCountRAMFilter() != computer.getRamsCount() && SharedData.getCountRAMFilter() != 0) {
                continue;
            }

            if (SharedData.getCoolerFilter() != null && SharedData.getCoolerFilter().getId() != computer.getCoolerId()) {
                continue;
            }

            if (SharedData.getCaseFilter() != null && SharedData.getCaseFilter().getId() != computer.getCaseId()) {
                continue;
            }




            
            int result = new BigDecimal(SharedData.getMinCostFilter()).compareTo(computer.getPrice());
            if (result == 1) {
                continue;
            }

            int result2 = new BigDecimal(SharedData.getMaxCostFilter()).compareTo(computer.getPrice());
            if (result2 == -1) {
                continue;
            }



            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shop/main_pages/childrens/computerItem.fxml"));
                Parent computerItem = loader.load();
                ComputerItemController controller = loader.getController();
                controller.setProductData(computer);
                itemsList.getChildren().add(computerItem);
                count++;
            } catch (IOException ex) {
                Logger.getLogger(CatalogsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }

        if (count == 0) {
            Label label = new Label();
            label.setFont(Font.font("Arial", 30));
            label.setText("Catalog is empty");
            itemsList.getChildren().add(label);
            scrollPane.setFitToHeight(true);
        }

        VBox.setVgrow(itemsList, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
    }
}
