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
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

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
        setupColumn(nameColumn, "name", Processor::setName);
        setupColumn(brandColumn, "brand", Processor::setBrand);
        setupIntegerColumn(coresColumn, "cores", Processor::setCores);
        setupIntegerColumn(threadsColumn, "threads", Processor::setThreads);
        setupBigDecimalColumn(baseClockColumn, "baseClock", Processor::setBaseClock);
        setupBigDecimalColumn(boostClockColumn, "boostClock", Processor::setBoostClock);
        setupColumn(linkColumn, "link", Processor::setLink);

        loadData();
        tableView.setItems(processorsList);
    }

    private void setupColumn(TableColumn<Processor, String> column, String column_name, BiConsumer<Processor, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            setter.accept(processor, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<Processor, Integer> column, String column_name, BiConsumer<Processor, Integer> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                setter.accept(processor, event.getNewValue());
            } catch (Exception e) {
                showErrorAlert("The value in the “" + column.getText() + "” column must be a number");
            }
        });
    }

    private void setupBigDecimalColumn(TableColumn<Processor, BigDecimal> column, String column_name, BiConsumer<Processor, BigDecimal> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        column.setOnEditCommit(event -> {
            Processor processor = event.getRowValue();
            try {
                setter.accept(processor, event.getNewValue());
            } catch (Exception e) {
                showErrorAlert("The value in the “" + column.getText() + "” column must be a decimal");
            }
        });
    }

    private void showErrorAlert(String message) {
        window = addButton.getScene().getWindow();
        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", message);
    }

    private void loadData() {
        processorsList.clear();
        processorsList.addAll(DbConnection.getDatabaseConnection().getAllProcessors());
    }

    private boolean isValid(Processor proc) {
        return proc.getName() != null && !proc.getName().isEmpty() &&
               proc.getBrand() != null && !proc.getBrand().isEmpty() &&
               proc.getCores() != null &&
               proc.getThreads() != null &&
               proc.getBaseClock() != null &&
               proc.getBoostClock() != null &&
               proc.getLink() != null && !proc.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Processor proc : processorsList) {
            if (isValid(proc)) {
                if (proc.getId() == null) {
                    DbConnection.getDatabaseConnection().addProcessor(proc);
                } else {
                    DbConnection.getDatabaseConnection().updateProcessor(proc);
                }
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
