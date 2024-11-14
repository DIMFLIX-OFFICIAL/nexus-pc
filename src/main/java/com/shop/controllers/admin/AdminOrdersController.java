package com.shop.controllers.admin;

import com.shop.controllers.SharedData;
import com.shop.database.DbConnection;
import com.shop.database.models.Order;
import com.shop.database.models.User;
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
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


class TimestampStringConverter extends StringConverter<Timestamp> {
    private final DateTimeFormatter formatter;

    public TimestampStringConverter() {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public String toString(Timestamp timestamp) {
        return timestamp != null ? LocalDateTime.ofInstant(timestamp.toInstant(), java.time.ZoneId.systemDefault()).format(formatter) : "";
    }

    @Override
    public Timestamp fromString(String string) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(string, formatter);
            return Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            return null;
        }
    }
}

public class AdminOrdersController implements Initializable {
    @FXML
    private TableView<Order> tableView;
    @FXML
    private TableColumn<Order, Integer> idColumn;
    @FXML
    private TableColumn<Order, String> customerColumn;
    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;
    @FXML
    private TableColumn<Order, BigDecimal> totalAmountColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;

    Window window;

    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<Order> ordersList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupIntegerColumn(idColumn, "id",  Order::setId);
        idColumn.setEditable(false);

        ObservableList<String> usernames = FXCollections.observableArrayList(
            DbConnection.getDatabaseConnection().getAllUsers()
            .stream()
            .map(User::getUsername) // Применяем метод getUsername к каждому пользователю
            .collect(Collectors.toList()) // Собираем результаты в список
        );
        setupComboColumn(customerColumn, "customer", usernames,  Order::setCustomer);

        setupTimestampColumn(orderDateColumn, "orderDate", Order::setOrderDate);
        orderDateColumn.setEditable(false);

        setupBigDecimalColumn(totalAmountColumn, "totalAmount", Order::setTotalAmount);
        totalAmountColumn.setEditable(false);

        ObservableList<String> statuses = FXCollections.observableArrayList();
        statuses.addAll("Pending", "Delivered");
        setupComboColumn(statusColumn, "status", statuses, Order::setStatus);

        loadData();
        tableView.setItems(ordersList);
    }

    private void setupIntegerColumn(TableColumn<Order, Integer> column, String column_name, BiConsumer<Order, Integer> setter) {
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
            Order value = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(value, newValue);
            }
        });
    }

    private void setupBigDecimalColumn(TableColumn<Order, BigDecimal> column, String column_name, BiConsumer<Order, BigDecimal> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter() {
            @Override
            public BigDecimal fromString(String string) {
                try {
                    return super.fromString(string);
                } catch (NumberFormatException e) {
                    AlertHelper.showErrorAlert("The value in the “" + column.getText() + "” column must be a decimal");
                    return null;
                }
            }
        }));
    
        column.setOnEditCommit(event -> {
            Order value = event.getRowValue();
            BigDecimal newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(value, newValue);
            }
        });
    }

    private <T> void setupComboColumn(TableColumn<Order, T> column, String columnName,
                                       ObservableList<T> items,
                                       BiConsumer<Order, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(items));
        column.setOnEditCommit(event -> {
            Order order = event.getRowValue();
            T selectedItem = event.getNewValue();

            if (selectedItem!= null) {
                setter.accept(order, selectedItem.toString());
            } else {
                AlertHelper.showErrorAlert("Unable to set a non-existent value");
            }
        });
    }

    private void setupTimestampColumn(TableColumn<Order, Timestamp> column, String columnName, BiConsumer<Order, Timestamp> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
    
        column.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));
    
        column.setOnEditCommit(event -> {
            Order value = event.getRowValue();
            Timestamp newValue = event.getNewValue();
            if (newValue != null) {
                setter.accept(value, newValue);
            }
        });
    }

    private void loadData() {
        ordersList.clear();
        ordersList.addAll(DbConnection.getDatabaseConnection().getUserOrders(
            SharedData.getAuthenticatedUser().getUsername())
        );
    }

    private boolean isValid(Order order) {
        return order.getCustomer() != null && !order.getCustomer().isEmpty() &&
               order.getStatus() != null && !order.getStatus().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (Order order : ordersList) {
            if (isValid(order)) {
                if (order.getId() != null) {
                    DbConnection.getDatabaseConnection().updateOrder(order);
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
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            if (selectedOrder.getId() != null) {
                if (DbConnection.getDatabaseConnection().deleteOrder(selectedOrder.getId())) {
                    ordersList.remove(selectedOrder);
                }
            } else {
                ordersList.remove(selectedOrder);
            }
            tableView.refresh();
        }
    }
}
