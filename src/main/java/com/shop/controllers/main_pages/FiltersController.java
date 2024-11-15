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
import com.shop.database.models.Case;
import com.shop.database.models.Computer;
import com.shop.database.models.Cooler;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Motherboard;
import com.shop.database.models.OrderResult;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.Processor;
import com.shop.database.models.RAM;
import com.shop.database.models.ShoppingCartItem;
import com.shop.helper.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FiltersController implements Initializable {
    @FXML
    AnchorPane root;
    @FXML
    ComboBox<Processor> processorFilter;
    @FXML
    ComboBox<GraphicCard> graphicCardFilter;
    @FXML
    ComboBox<Motherboard> motherboardFilter;
    @FXML
    ComboBox<PowerSupply> powerSupplyFilter;
    @FXML
    ComboBox<RAM> ramFilter;
    @FXML
    Spinner<Integer> ramsCountInput;

    @FXML
    ComboBox<Cooler> coolerFilter;
    @FXML
    ComboBox<Case> caseFilter;

    @FXML
    Spinner<Integer> minCostInput;
    @FXML
    Spinner<Integer> maxCostInput;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SpinnerValueFactory<Integer> minCostFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1,
            1000000000,
            SharedData.getMinCostFilter()
        );
        minCostInput.setValueFactory(minCostFactory);
        minCostInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setMinCostFilter(newValue);
        });

        SpinnerValueFactory<Integer> maxCostFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
            1,
            1000000000,
            SharedData.getMaxCostFilter()
        );
        maxCostInput.setValueFactory(maxCostFactory);
        maxCostInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setMaxCostFilter(newValue);
        });




        ObservableList<Processor> processors = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllProcessors());
        processorFilter.setItems(processors);
        Processor proc_value = SharedData.getProcessorFilter();
        if (proc_value != null) {
            processorFilter.setValue(proc_value);
        }
        processorFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setProcessorFilter(newValue);
        });
        


        ObservableList<GraphicCard> gpus = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllGraphicCards());
        graphicCardFilter.setItems(gpus);
        GraphicCard gpu_value = SharedData.getGraphicCardFilter();
        if (gpu_value != null) {
            graphicCardFilter.setValue(gpu_value);
        }
        graphicCardFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setGraphicCardFilter(newValue);
        });

        ObservableList<Motherboard> motherboards = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllMotherboards());
        motherboardFilter.setItems(motherboards);
        Motherboard motherboard_value = SharedData.getMotherboardFilter();
        if (motherboard_value != null) {
            motherboardFilter.setValue(motherboard_value);
        }
        motherboardFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setMotherboardFilter(newValue);
        });

        ObservableList<RAM> rams = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllRAMs());
        ramFilter.setItems(rams);
        RAM ram_value = SharedData.getRamFilter();
        if (ram_value != null) {
            ramFilter.setValue(ram_value);
        }
        ramFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setRamFilter(newValue);
        });

        SpinnerValueFactory<Integer> ramsCountFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
            0,
            1000000000,
            0
        );
        ramsCountInput.setValueFactory(ramsCountFactory);
        ramsCountInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setCountRAMFilter(newValue);
        });

        ObservableList<PowerSupply> powerSuplies = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllPowerSupplies());
        powerSupplyFilter.setItems(powerSuplies);
        PowerSupply ps_value = SharedData.getPowerSupplyFilter();
        if (ps_value != null) {
            powerSupplyFilter.setValue(ps_value);
        }
        powerSupplyFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setPowerSupplyFilter(newValue);
        });

        ObservableList<Cooler> coolers = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllCoolers());
        coolerFilter.setItems(coolers);
        Cooler cooler_value = SharedData.getCoolerFilter();
        if (cooler_value != null) {
            coolerFilter.setValue(cooler_value);
        }
        coolerFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setCoolerFilter(newValue);
        });

        ObservableList<Case> cases = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllCases());
        caseFilter.setItems(cases);
        Case case_value = SharedData.getCaseFilter();
        if (case_value != null) {
            caseFilter.setValue(case_value);
        }
        caseFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            SharedData.setCaseFilter(newValue);
        });
    }
}
