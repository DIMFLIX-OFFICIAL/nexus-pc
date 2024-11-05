package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.Cooler;
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

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AdminCoolersController implements Initializable {
    @FXML
    private TableView<Cooler> tableView;
    @FXML
    private TableColumn<Cooler, String> nameColumn;
    @FXML
    private TableColumn<Cooler, String> brandColumn;
    @FXML
    private TableColumn<Cooler, String> typeColumn;
    @FXML
    private TableColumn<Cooler, BigDecimal> coolingCapacityColumn;
    @FXML
    private TableColumn<Cooler, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Cooler> coolersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", Cooler::setName);
        setupColumn(brandColumn, "brand", Cooler::setBrand);
        setupColumn(typeColumn, "type", Cooler::setType);
        setupBigDecimalColumn(coolingCapacityColumn, "coolingCapacity", Cooler::setCoolingCapacity);
        setupColumn(linkColumn, "link", Cooler::setLink);

        loadData();
        tableView.setItems(coolersList);
    }

    private void setupColumn(TableColumn<Cooler, String> column, String column_name, BiConsumer<Cooler, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            Cooler cooler = event.getRowValue();
            setter.accept(cooler, event.getNewValue());
        });
    }

    private void setupBigDecimalColumn(TableColumn<Cooler, BigDecimal> column, String column_name, BiConsumer<Cooler, BigDecimal> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
            @Override
            public BigDecimal fromString(String string) {
                try {
                    return super.fromString(string);
                } catch (NumberFormatException e) {
                    showErrorAlert("The value in the “" + column.getText() + "” column must be a decimal");
                    return null;
                }
            }
        }));
    
        column.setOnEditCommit(event -> {
            Cooler cooler = event.getRowValue();
            BigDecimal newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(cooler, newValue);
            }
        });
    }

    private void showErrorAlert(String message) {
        window = addButton.getScene().getWindow();
        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", message);
    }

    private void loadData() {
        coolersList.clear();
        coolersList.addAll(DbConnection.getDatabaseConnection().getAllCoolers());
    }

    private boolean isValid(Cooler cooler) {
        return cooler.getName() != null && !cooler.getName().isEmpty() &&
               cooler.getBrand() != null && !cooler.getBrand().isEmpty() &&
               cooler.getType() != null && !cooler.getType().isEmpty() &&
               cooler.getCoolingCapacity() != null &&
               cooler.getLink() != null && !cooler.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Cooler cooler : coolersList) {
            if (isValid(cooler)) {
                if (cooler.getId() == null) {
                    DbConnection.getDatabaseConnection().addCooler(cooler);
                } else {
                    DbConnection.getDatabaseConnection().updateCooler(cooler);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        Cooler newCooler = new Cooler(null, null, null, null, null, null);
        coolersList.add(newCooler);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        Cooler selectedCooler = tableView.getSelectionModel().getSelectedItem();
        if (selectedCooler != null) {
            coolersList.remove(selectedCooler);
            if (selectedCooler.getId() != null) {
                DbConnection.getDatabaseConnection().deleteCooler(selectedCooler.getId());
            }
            tableView.refresh();
        }
    }
}
