package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.RAM;
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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AdminRAMsController implements Initializable {
    @FXML
    private TableView<RAM> tableView;
    @FXML
    private TableColumn<RAM, String> nameColumn;
    @FXML
    private TableColumn<RAM, String> brandColumn;
    @FXML
    private TableColumn<RAM, Integer> capacityColumn;
    @FXML
    private TableColumn<RAM, Integer> speedColumn;
    @FXML
    private TableColumn<RAM, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private ObservableList<RAM> ramsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", RAM::setName);
        setupColumn(brandColumn, "brand", RAM::setBrand);
        setupIntegerColumn(capacityColumn, "capacity", RAM::setCapacity);
        setupIntegerColumn(speedColumn, "speed", RAM::setSpeed);
        setupColumn(linkColumn, "link", RAM::setLink);

        loadData();
        tableView.setItems(ramsList);
    }

    private void setupColumn(TableColumn<RAM, String> column, String column_name, BiConsumer<RAM, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            RAM ram = event.getRowValue();
            setter.accept(ram, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<RAM, Integer> column, String column_name, BiConsumer<RAM, Integer> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
            @Override
            public Integer fromString(String string) {
                try {
                    return super.fromString(string);
                } catch (NumberFormatException e) {
                    showErrorAlert("The value in the “" + column.getText() + "” column must be a number");
                    return null;
                }
            }
        }));
        
        column.setOnEditCommit(event -> {
            RAM ram = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(ram, newValue);
            }
        });
    }

    private void showErrorAlert(String message) {
        window = addButton.getScene().getWindow();
        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", message);
    }

    private void loadData() {
        ramsList.clear();
        ramsList.addAll(DbConnection.getDatabaseConnection().getAllRAMs());
    }

    private boolean isValid(RAM ram) {
        return ram.getName() != null && !ram.getName().isEmpty() &&
               ram.getBrand() != null && !ram.getBrand().isEmpty() &&
               ram.getCapacity() != null &&
               ram.getSpeed() != null &&
               ram.getLink() != null && !ram.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (RAM ram : ramsList) {
            if (isValid(ram)) {
                if (ram.getId() == null) {
                    DbConnection.getDatabaseConnection().addRAM(ram);
                } else {
                    DbConnection.getDatabaseConnection().updateRAM(ram);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        RAM newRAM = new RAM(null, null, null, null, null, null);
        ramsList.add(newRAM);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        RAM selectedRAM = tableView.getSelectionModel().getSelectedItem();
        if (selectedRAM != null) {
            ramsList.remove(selectedRAM);
            if (selectedRAM.getId() != null) {
                DbConnection.getDatabaseConnection().deleteRAM(selectedRAM.getId());
            }
            tableView.refresh();
        }
    }
}
