package com.shop.controllers.admin;

import com.shop.database.DbConnection;
import com.shop.database.models.User; // Ensure you have the User model imported
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ResourceBundle;

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

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        usernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setUsername(event.getNewValue());
        });

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setEmail(event.getNewValue());
        });

        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setFirstName(event.getNewValue());
        });

        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setLastName(event.getNewValue());
        });

        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setPassword(event.getNewValue());
        });

        roleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        roleColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setRole(event.getNewValue());
        });

        loadData();
        tableView.setItems(userList);
    }

    private void loadData() {
        userList.clear();
        userList.addAll(DbConnection.getDatabaseConnection().getAllUsers());
    }

    @FXML
    private void handleSave() {
        for (User user : userList) {
            if (
                user.getPassword() == null || user.getPassword().isEmpty() || 
                user.getRole() == null || user.getRole().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() || 
                user.getFirstName() == null || user.getFirstName().isEmpty() || 
                user.getLastName() == null || user.getLastName().isEmpty()) {
                continue;
            }

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

        loadData();
    }

    @FXML
    private void handleAdd() {
        User newUser = new User(null, null, null, null, null, "user");
        userList.add(newUser);
        tableView.refresh();
    }

    @FXML
    private void handleDelete() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userList.remove(selectedUser);

            if (DbConnection.getDatabaseConnection().deleteUser(selectedUser.getUsername())) {
                System.out.println("Deleted: " + selectedUser.getUsername());
            }

            tableView.refresh();
        }
    }
}
