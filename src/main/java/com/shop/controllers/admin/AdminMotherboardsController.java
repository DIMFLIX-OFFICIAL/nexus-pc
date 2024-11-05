package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Motherboard;
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

public class AdminMotherboardsController implements Initializable {
    @FXML
    private TableView<Motherboard> tableView;
    @FXML
    private TableColumn<Motherboard, String> nameColumn;
    @FXML
    private TableColumn<Motherboard, String> brandColumn;
    @FXML
    private TableColumn<Motherboard, String> socketTypeColumn;
    @FXML
    private TableColumn<Motherboard, String> formFactorColumn;
    @FXML
    private TableColumn<Motherboard, Integer> maxMemoryColumn;
    @FXML
    private TableColumn<Motherboard, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Motherboard> motherboardsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", Motherboard::setName);
        setupColumn(brandColumn, "brand", Motherboard::setBrand);
        setupColumn(socketTypeColumn, "socketType", Motherboard::setSocketType);
        setupColumn(formFactorColumn, "formFactor", Motherboard::setFormFactor);
        setupIntegerColumn(maxMemoryColumn, "maxMemory", Motherboard::setMaxMemory);
        setupColumn(linkColumn, "link", Motherboard::setLink);

        loadData();
        tableView.setItems(motherboardsList);
    }

    private void setupColumn(TableColumn<Motherboard, String> column, String column_name, BiConsumer<Motherboard, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            Motherboard motherboard = event.getRowValue();
            setter.accept(motherboard, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<Motherboard, Integer> column, String column_name, BiConsumer<Motherboard, Integer> setter) {
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
            Motherboard motherboard = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(motherboard, newValue);
            }
        });
    }

    private void showErrorAlert(String message) {
        window = addButton.getScene().getWindow();
        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", message);
    }

    private void loadData() {
        motherboardsList.clear();
        motherboardsList.addAll(DbConnection.getDatabaseConnection().getAllMotherboards());
    }

    private boolean isValid(Motherboard motherboard) {
        return motherboard.getName() != null && !motherboard.getName().isEmpty() &&
               motherboard.getBrand() != null && !motherboard.getBrand().isEmpty() &&
               motherboard.getSocketType() != null && !motherboard.getSocketType().isEmpty() &&
               motherboard.getFormFactor() != null && !motherboard.getFormFactor().isEmpty() &&
               motherboard.getMaxMemory() != null &&
               motherboard.getLink() != null && !motherboard.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Motherboard motherboard : motherboardsList) {
            if (isValid(motherboard)) {
                if (motherboard.getId() == null) {
                    DbConnection.getDatabaseConnection().addMotherboard(motherboard);
                } else {
                    DbConnection.getDatabaseConnection().updateMotherboard(motherboard);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        Motherboard newMotherboard = new Motherboard(null, null, null, null, null, null, null);
        motherboardsList.add(newMotherboard);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        Motherboard selectedMotherboard = tableView.getSelectionModel().getSelectedItem();
        if (selectedMotherboard != null) {
            motherboardsList.remove(selectedMotherboard);
            if (selectedMotherboard.getId() != null) {
                DbConnection.getDatabaseConnection().deleteMotherboard(selectedMotherboard.getId());
            }
            tableView.refresh();
        }
    }
}
