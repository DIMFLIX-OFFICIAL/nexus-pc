package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.PowerSupply;
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

public class AdminPowerSuppliesController implements Initializable {
    @FXML
    private TableView<PowerSupply> tableView;
    @FXML
    private TableColumn<PowerSupply, String> nameColumn;
    @FXML
    private TableColumn<PowerSupply, String> brandColumn;
    @FXML
    private TableColumn<PowerSupply, Integer> wattageColumn;
    @FXML
    private TableColumn<PowerSupply, String> efficiencyRatingColumn;
    @FXML
    private TableColumn<PowerSupply, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<PowerSupply> powerSuppliesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", PowerSupply::setName);
        setupColumn(brandColumn, "brand", PowerSupply::setBrand);
        setupIntegerColumn(wattageColumn, "wattage", PowerSupply::setWattage);
        setupColumn(efficiencyRatingColumn, "efficiencyRating", PowerSupply::setEfficiencyRating);
        setupColumn(linkColumn, "link", PowerSupply::setLink);

        loadData();
        tableView.setItems(powerSuppliesList);
    }

    private void setupColumn(TableColumn<PowerSupply, String> column, String column_name, BiConsumer<PowerSupply, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            PowerSupply ps = event.getRowValue();
            setter.accept(ps, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<PowerSupply, Integer> column, String column_name, BiConsumer<PowerSupply, Integer> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
            @Override
            public Integer fromString(String string) {
                try {
                    return super.fromString(string);
                } catch (NumberFormatException e) {
                    AlertHelper.showErrorAlert("The value in the “" + column.getText() + "” column must be a number");
                    return null;
                }
            }
        }));
        
        column.setOnEditCommit(event -> {
            PowerSupply ps = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(ps, newValue);
            }
        });
    }

    private void loadData() {
        powerSuppliesList.clear();
        powerSuppliesList.addAll(DbConnection.getDatabaseConnection().getAllPowerSupplies());
    }

    private boolean isValid(PowerSupply ps) {
        return ps.getName() != null && !ps.getName().isEmpty() &&
               ps.getBrand() != null && !ps.getBrand().isEmpty() &&
               ps.getWattage() != null &&
               ps.getEfficiencyRating() != null && !ps.getEfficiencyRating().isEmpty() &&
               ps.getLink() != null && !ps.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (PowerSupply ps : powerSuppliesList) {
            if (isValid(ps)) {
                if (ps.getId() == null) {
                    DbConnection.getDatabaseConnection().addPowerSupply(ps);
                } else {
                    DbConnection.getDatabaseConnection().updatePowerSupply(ps);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        PowerSupply newPS = new PowerSupply(null, null, null, null, null, null);
        powerSuppliesList.add(newPS);
        tableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        PowerSupply selectedPS = tableView.getSelectionModel().getSelectedItem();
        if (selectedPS != null) {
            powerSuppliesList.remove(selectedPS);
            if (selectedPS.getId() != null) {
                DbConnection.getDatabaseConnection().deletePowerSupply(selectedPS.getId());
            }
            tableView.refresh();
        }
    }
}
