package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.GraphicCard;
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

public class AdminGraphicCardsController implements Initializable {
    @FXML
    private TableView<GraphicCard> tableView;
    @FXML
    private TableColumn<GraphicCard, String> nameColumn;
    @FXML
    private TableColumn<GraphicCard, String> brandColumn;
    @FXML
    private TableColumn<GraphicCard, Integer> memorySizeColumn;
    @FXML
    private TableColumn<GraphicCard, String> memoryTypeColumn;
    @FXML
    private TableColumn<GraphicCard, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<GraphicCard> graphicCardsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", GraphicCard::setName);
        setupColumn(brandColumn, "brand", GraphicCard::setBrand);
        setupIntegerColumn(memorySizeColumn, "memorySize", GraphicCard::setMemorySize);
        setupColumn(memoryTypeColumn, "memoryType", GraphicCard::setMemoryType);
        setupColumn(linkColumn, "link", GraphicCard::setLink);

        loadData();
        tableView.setItems(graphicCardsList);
    }

    private void setupColumn(TableColumn<GraphicCard, String> column, String column_name, BiConsumer<GraphicCard, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            GraphicCard gpu = event.getRowValue();
            setter.accept(gpu, event.getNewValue());
        });
    }

    private void setupIntegerColumn(TableColumn<GraphicCard, Integer> column, String column_name, BiConsumer<GraphicCard, Integer> setter) {
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
            GraphicCard gpu = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(gpu, newValue);
            }
        });
    }

    private void loadData() {
        graphicCardsList.clear();
        graphicCardsList.addAll(DbConnection.getDatabaseConnection().getAllGraphicCards());
    }

    private boolean isValid(GraphicCard gpu) {
        return gpu.getName() != null && !gpu.getName().isEmpty() &&
               gpu.getBrand() != null && !gpu.getBrand().isEmpty() &&
               gpu.getMemorySize() != null &&
               gpu.getMemoryType() != null && !gpu.getMemoryType().isEmpty() &&
               gpu.getLink() != null && !gpu.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (GraphicCard gpu : graphicCardsList) {
            if (isValid(gpu)) {
                if (gpu.getId() == null) {
                    DbConnection.getDatabaseConnection().addGraphicCard(gpu);
                } else {
                    DbConnection.getDatabaseConnection().updateGraphicCard(gpu);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        GraphicCard newGPU = new GraphicCard(null, null, null, null, null, null);
        graphicCardsList.add(newGPU);
        tableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        GraphicCard selectedGPU = tableView.getSelectionModel().getSelectedItem();
        if (selectedGPU != null) {
            graphicCardsList.remove(selectedGPU);
            if (selectedGPU.getId() != null) {
                DbConnection.getDatabaseConnection().deleteGraphicCard(selectedGPU.getId());
            }
            tableView.refresh();
        }
    }
}
