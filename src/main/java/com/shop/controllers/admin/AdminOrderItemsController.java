package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.OrderItem;
import com.shop.database.models.Cooler;
import com.shop.database.models.GraphicCard;
import com.shop.database.models.Identifiable;
import com.shop.database.models.Motherboard;
import com.shop.database.models.Order;
import com.shop.database.models.OrderItem;
import com.shop.database.models.PowerSupply;
import com.shop.database.models.Processor;
import com.shop.database.models.RAM;
import com.shop.database.models.Case;
import com.shop.database.models.Computer;
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
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;


public class AdminOrderItemsController implements Initializable {
    @FXML
    private TableView<OrderItem> tableView;
    @FXML
    private TableColumn<OrderItem, Integer> idColumn;
    @FXML
    private TableColumn<OrderItem, Integer> orderIdColumn;
    @FXML
    private TableColumn<OrderItem, Computer> computerColumn;
    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<OrderItem> orderItemsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupIntegerColumn(idColumn, "id", OrderItem::setId);
        idColumn.setEditable(false);

        setupIntegerColumn(orderIdColumn, "orderId", OrderItem::setOrderId);
        idColumn.setEditable(false);

        ObservableList<Computer> computers = FXCollections.observableArrayList(DbConnection.getDatabaseConnection().getAllComputers());
        setupComboColumn(computerColumn, "computer", computers, OrderItem::setComputer);
        computerColumn.setEditable(false);

        setupIntegerColumn(quantityColumn, "quantity", OrderItem::setQuantity);
        quantityColumn.setEditable(true);

        loadData();
        tableView.setItems(orderItemsList);
    }

    private void setupIntegerColumn(TableColumn<OrderItem, Integer> column, String column_name, BiConsumer<OrderItem, Integer> setter) {
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
            OrderItem pc = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(pc, newValue);
            }
        });
    }

    private <T> void setupComboColumn(TableColumn<OrderItem, T> column, String columnName,
                                       ObservableList<T> items,
                                       BiConsumer<OrderItem, T> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(items));
        column.setOnEditCommit(event -> {
            OrderItem orderItem = event.getRowValue();
            T selectedItem = event.getNewValue();

            if (selectedItem!= null) {
                setter.accept(orderItem, selectedItem);
            } else {
                AlertHelper.showErrorAlert("Unable to set a non-existent value");
            }
        });
    }

    private void loadData() {
        orderItemsList.clear();
        orderItemsList.addAll(DbConnection.getDatabaseConnection().getAllOrderItems());
    }

    private boolean isValid(OrderItem item) {
        return item.getOrderId() != null &&
               item.getComputer() != null &&
               item.getQuantity() != null;
    }

    @FXML
    private void handleSave() {
        for (OrderItem item : orderItemsList) {
            if (isValid(item)) {
                if (item.getId() != null) {
                    DbConnection.getDatabaseConnection().updateOrderItem(item);
                }
            }
        }
        loadData();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        OrderItem selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (selectedItem.getId() != null) {
                if (DbConnection.getDatabaseConnection().deleteComputer(selectedItem.getId())) {
                    orderItemsList.remove(selectedItem);
                }
            } else {
                orderItemsList.remove(selectedItem);
            }
            tableView.refresh();
        }
    }
}
