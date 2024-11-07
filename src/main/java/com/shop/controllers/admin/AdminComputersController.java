package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.Cooler;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Identifiable;
import com.shop.database.models.Motherboard;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.Processor;
import com.shop.database.models.RAM;
import com.shop.database.models.Case;
import com.shop.helper.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Window;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;


public class AdminComputersController implements Initializable {
    @FXML
    private TableView<Computer> tableView;
    @FXML
    private TableColumn<Computer, String> nameColumn;
    @FXML
    private TableColumn<Computer, String> descriptionColumn;
    @FXML
    private TableColumn<Computer, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Computer, Processor> processorIdColumn;
    @FXML
    private TableColumn<Computer, GraphicCard> graphicCardIdColumn;
    @FXML
    private TableColumn<Computer, Motherboard> motherboardIdColumn;
    @FXML
    private TableColumn<Computer, RAM> ramIdColumn;
    @FXML
    private TableColumn<Computer, Integer> ramsCountColumn;
    @FXML
    private TableColumn<Computer, PowerSupply> powerSupplyIdColumn;
    @FXML
    private TableColumn<Computer, Cooler> coolerIdColumn;
    @FXML
    private TableColumn<Computer, Case> caseIdColumn;
    @FXML
    private TableColumn<Computer, String> imageUrlColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Computer> computersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", Computer::setName);
        setupColumn(descriptionColumn, "description", Computer::setDescription);
        setupBigDecimalColumn(priceColumn, "price", Computer::setPrice);
        
        ObservableList<Processor> processors = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllProcessors());
        setupComboColumn(processorIdColumn, "processorId", processors, Computer::setProcessorId);

        ObservableList<GraphicCard> gpus = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllGraphicCards());
        setupComboColumn(graphicCardIdColumn, "graphicCardId", gpus, Computer::setGraphicCardId);

        ObservableList<Motherboard> motherboards = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllMotherboards());
        setupComboColumn(motherboardIdColumn, "motherboardId", motherboards, Computer::setMotherboardId);

        ObservableList<RAM> rams = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllRAMs());
        setupComboColumn(ramIdColumn, "ramId", rams, Computer::setRamId);
        setupIntegerColumn(ramsCountColumn, "ramsCount", Computer::setRamsCount);

        ObservableList<PowerSupply> powerSuplies = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllPowerSupplies());
        setupComboColumn(powerSupplyIdColumn, "powerSupplyId", powerSuplies, Computer::setPowerSupplyId);

        ObservableList<Cooler> coolers = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllCoolers());
        setupComboColumn(coolerIdColumn, "coolerId", coolers, Computer::setCoolerId);

        ObservableList<Case> cases = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllCases());
        setupComboColumn(caseIdColumn, "caseId", cases, Computer::setCaseId);

        setupColumn(imageUrlColumn, "imageUrl", Computer::setImageUrl);
        
        loadData();
        tableView.setItems(computersList);
    }

    private void setupColumn(TableColumn<Computer, String> column, String column_name, BiConsumer<Computer, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            Computer pc = event.getRowValue();
            setter.accept(pc, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<Computer, Integer> column, String column_name, BiConsumer<Computer, Integer> setter) {
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
            Computer pc = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(pc, newValue);
            }
        });
    }

    private void setupBigDecimalColumn(TableColumn<Computer, BigDecimal> column, String column_name, BiConsumer<Computer, BigDecimal> setter) {
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
            Computer pc = event.getRowValue();
            BigDecimal newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(pc, newValue);
            }
        });
    }

    private <T extends Identifiable> void setupComboColumn(TableColumn<Computer, T> column, String columnName,
                                       ObservableList<T> items,
                                       BiConsumer<Computer, Integer> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(items));
        column.setOnEditCommit(event -> {
            Computer pc = event.getRowValue();
            T selectedItem = event.getNewValue();

            if (selectedItem!= null) {
                setter.accept(pc, selectedItem.getId());
            } else {
                showErrorAlert("Unable to set a non-existent value");
            }
        });
    }

    private void showErrorAlert(String message) {
        window = addButton.getScene().getWindow();
        AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error", message);
    }

    private void loadData() {
        computersList.clear();
        computersList.addAll(DbConnection.getDatabaseConnection().getAllComputers());
    }

    private boolean isValid(Computer pc) {
        return pc.getName() != null && !pc.getName().isEmpty() &&
               pc.getDescription() != null && !pc.getDescription().isEmpty() &&
               pc.getPrice() != null &&
               pc.getProcessorId() != null &&
               pc.getGraphicCardId() != null &&
               pc.getMotherboardId() != null &&
               pc.getRamId() != null &&
               pc.getRamsCount() != null &&
               pc.getPowerSupplyId() != null &&
               pc.getCoolerId() != null &&
               pc.getCaseId() != null &&
               pc.getImageUrl() != null && !pc.getImageUrl().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Computer pc : computersList) {
            if (isValid(pc)) {
                if (pc.getId() == null) {
                    DbConnection.getDatabaseConnection().addComputer(pc);
                } else {
                    DbConnection.getDatabaseConnection().updateComputer(pc);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        Computer newRAM = new Computer(null, null, null, null, null, null, null, null, null, null, null, null, null);
        computersList.add(newRAM);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        Computer selectedPC = tableView.getSelectionModel().getSelectedItem();
        if (selectedPC != null) {
            computersList.remove(selectedPC);
            if (selectedPC.getId() != null) {
                DbConnection.getDatabaseConnection().deleteComputer(selectedPC.getId());
            }
            tableView.refresh();
        }
    }
}
