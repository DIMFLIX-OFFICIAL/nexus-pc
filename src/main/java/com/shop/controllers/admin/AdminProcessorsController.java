package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.Processor;
import com.shop.helper.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.BigDecimalStringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminProcessorsController implements Initializable {
    @FXML
    private TableView<Processor> tableView;
    @FXML
    private TableColumn<Processor, String> nameColumn;
    @FXML
    private TableColumn<Processor, String> brandColumn;
    @FXML
    private TableColumn<Processor, Integer> coresColumn;
    @FXML
    private TableColumn<Processor, Integer> threadsColumn;
    @FXML
    private TableColumn<Processor, BigDecimal> baseClockColumn;
    @FXML
    private TableColumn<Processor, BigDecimal> boostClockColumn;
    @FXML
    private TableColumn<Processor, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Processor> processorsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            processor.setName(event.getNewValue());
        });
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        brandColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        brandColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            processor.setBrand(event.getNewValue());
        });
        coresColumn.setCellValueFactory(new PropertyValueFactory<>("cores"));
        coresColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        coresColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                processor.setCores(event.getNewValue());
            } catch(Exception e) {
                window = addButton.getScene().getWindow();
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "The value in the “cores” column must be a number");
            }
        });

        threadsColumn.setCellValueFactory(new PropertyValueFactory<>("threads"));
        threadsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        threadsColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                processor.setThreads(event.getNewValue());
            } catch(Exception e) {
                window = addButton.getScene().getWindow();
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "The value in the “threads” column must be a number");
            }
        });

        baseClockColumn.setCellValueFactory(new PropertyValueFactory<>("baseClock"));
        baseClockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        baseClockColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                processor.setBaseClock(event.getNewValue());
            } catch(Exception e) {
                window = addButton.getScene().getWindow();
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "The value in the “Base Clock” column must be a decimal");
            }
        });

        boostClockColumn.setCellValueFactory(new PropertyValueFactory<>("boostClock"));
        boostClockColumn.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        boostClockColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                processor.setBoostClock(event.getNewValue());
            } catch(Exception e) {
                window = addButton.getScene().getWindow();
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "The value in the “Boost Clock” column must be a decimal");
            }
        });

        linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
        linkColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        linkColumn.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            processor.setLink(event.getNewValue());
        });


        loadData();
        tableView.setItems(processorsList);
    }

    private void loadData() {
        processorsList.clear();
        processorsList.addAll(DbConnection.getDatabaseConnection().getAllProcessors());
    }

    @FXML
    private void handleSave() {
        for (Processor proc : processorsList) {
            if (
                proc.getName() == null || proc.getName().isEmpty() || 
                proc.getBrand() == null || proc.getBrand().isEmpty() ||
                proc.getCores() == null ||
                proc.getThreads() == null ||
                proc.getBaseClock() == null ||
                proc.getBoostClock() == null ||
                proc.getLink() == null || proc.getLink().isEmpty()) {
                continue;
            }

            if (proc.getId() == null) {
                DbConnection.getDatabaseConnection().addProcessor(proc);
            } else {
                DbConnection.getDatabaseConnection().updateProcessor(proc);
            }
        }

        loadData();
    }
 
    @FXML
    private void handleAdd() {
        Processor newProc = new Processor(null, null, null, null, null, null, null, null);
        processorsList.add(newProc);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        Processor selectedProcessor = tableView.getSelectionModel().getSelectedItem();
        if (selectedProcessor != null) {
            processorsList.remove(selectedProcessor);
            if (selectedProcessor.getId() != null) {
                DbConnection.getDatabaseConnection().deleteProcessor(selectedProcessor.getId());
            }
            tableView.refresh();
        }
    }
}
