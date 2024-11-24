package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.Computer;
import com.shop.database.models.Identifiable;
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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

public class AdminUsersController implements Initializable {
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumn(usernameColumn, "username", User::setUsername);
        setupColumn(emailColumn, "email", User::setEmail);
        setupColumn(firstNameColumn, "firstName", User::setFirstName);
        setupColumn(lastNameColumn, "lastName", User::setLastName);
        setupColumn(passwordColumn, "password", User::setPassword);

        ObservableList<String> roles = FXCollections.observableArrayList();
        roles.addAll("admin", "user");
        setupComboColumn(roleColumn, "role", roles, User::setRole);

        loadData();
        tableView.setItems(userList);
    }

    private void setupColumn(TableColumn<User, String> column, String column_name, BiConsumer<User, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(column_name));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            User processor = event.getRowValue();
            setter.accept(processor, event.getNewValue());
        });
    }

    private <T> void setupComboColumn(TableColumn<User, T> column, String columnName,
                                       ObservableList<T> items,
                                       BiConsumer<User, String> setter) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnName));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(items));
        column.setOnEditCommit(event -> {
            User user = event.getRowValue();
            T selectedItem = event.getNewValue();

            if (selectedItem!= null) {
                setter.accept(user, selectedItem.toString());
            } else {
                AlertHelper.showErrorAlert("Unable to set a non-existent value");
            }
        });
    }

    private void loadData() {
        userList.clear();
        userList.addAll(DbConnection.getDatabaseConnection().getAllUsers());
    }

    private boolean isValid(User user) {
        return user.getPassword() != null && !user.getPassword().isEmpty() && 
                user.getRole() != null && !user.getRole().isEmpty() &&
                user.getEmail() != null && !user.getEmail().isEmpty() &&
                user.getFirstName() != null && !user.getFirstName().isEmpty() && 
                user.getLastName() != null && !user.getLastName().isEmpty();
    }

    @FXML
    private void handleSave() {
        for (User user : userList) {
            if (isValid(user)) {
                if ((user.getUsername() == null && !user.getNewUsername().isEmpty())) {
                    User usr = new User(
                            user.getNewUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPassword(),
                            "user");
                    DbConnection.getDatabaseConnection().addUser(usr);
                } else if (user.getUsername() != null && !user.getUsername().equals(user.getNewUsername())) {
                    User usr = new User(
                            user.getNewUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getPassword(),
                            user.getRole(),
                            user.getCreatedAt());
                    if (DbConnection.getDatabaseConnection().deleteUser(user.getUsername())) {
                        DbConnection.getDatabaseConnection().addUser(usr);
                    }
                } else {
                    if (!user.getUsername().isEmpty()) {
                        DbConnection.getDatabaseConnection().updateUser(user);
                    }
                }
            }
        }

        loadData();
    }

    @FXML
    private void handleAdd() {
        User newUser = new User(null, null, null, null, null, "user");
        userList.add(newUser);
        tableView.refresh();
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleDelete() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userList.remove(selectedUser);

            if (selectedUser.getUsername() != null) {
                DbConnection.getDatabaseConnection().deleteUser(selectedUser.getUsername());
            }

            tableView.refresh();
        }
    }
}
