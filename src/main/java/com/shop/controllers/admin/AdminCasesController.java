package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.Case;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Window;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AdminCasesController implements Initializable {
    @FXML
    private TableView<Case> tableView;
    @FXML
    private TableColumn<Case, String> nameColumn;
    @FXML
    private TableColumn<Case, String> brandColumn;
    @FXML
    private TableColumn<Case, String> formFactorColumn;
    @FXML
    private TableColumn<Case, String> colorColumn;
    @FXML
    private TableColumn<Case, String> linkColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<Case> casesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(nameColumn, "name", Case::setName);
        setupColumn(brandColumn, "brand", Case::setBrand);
        setupColumn(formFactorColumn, "formFactor", Case::setFormFactor);
        setupColumn(colorColumn, "color", Case::setColor);
        setupColumn(linkColumn, "link", Case::setLink);

        loadData();
        tableView.setItems(casesList);
    }

    private void setupColumn(TableColumn<Case, String> column, String column_name, BiConsumer<Case, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            Case cs = event.getRowValue();
            setter.accept(cs, event.getNewValue());
        });
    }

    private void loadData() {
        casesList.clear();
        casesList.addAll(DbConnection.getDatabaseConnection().getAllCases());
    }

    private boolean isValid(Case cs) {
        return cs.getName() != null && !cs.getName().isEmpty() &&
               cs.getBrand() != null && !cs.getBrand().isEmpty() &&
               cs.getFormFactor() != null && !cs.getFormFactor().isEmpty() &&
               cs.getColor() != null && !cs.getColor().isEmpty() &&
               cs.getLink() != null && !cs.getLink().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Case cs : casesList) {
            if (isValid(cs)) {
                if (cs.getId() == null) {
                    DbConnection.getDatabaseConnection().addCase(cs);
                } else {
                    DbConnection.getDatabaseConnection().updateCase(cs);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleAdd() {
        Case newCase = new Case(null, null, null, null, null, null);
        casesList.add(newCase);
        tableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        Case selectedCase = tableView.getSelectionModel().getSelectedItem();
        if (selectedCase != null) {
            casesList.remove(selectedCase);
            if (selectedCase.getId() != null) {
                DbConnection.getDatabaseConnection().deleteCase(selectedCase.getId());
            }
            tableView.refresh();
        }
    }
}
