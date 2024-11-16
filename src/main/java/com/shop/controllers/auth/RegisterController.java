package com.shop.controllers.auth;

import com.shop.database.DbConnection;
import com.shop.helper.AlertHelper;
import com.shop.database.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;


public class RegisterController implements Initializable {
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField confirmPassword;

    @FXML
    private Button registerButton;

    @FXML
    private BorderPane rootPane;

    Window window;

    private double mouseX;
    private double mouseY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.setOnMousePressed(event -> {
            mouseX = event.getScreenX() - ((Stage) rootPane.getScene().getWindow()).getX();
            mouseY = event.getScreenY() - ((Stage) rootPane.getScene().getWindow()).getY();
        });

        rootPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - mouseX);
            stage.setY(event.getScreenY() - mouseY);
        });
    }

    @FXML
    private void register() {
        window = registerButton.getScene().getWindow();
        if (this.isValidated()) {
            DbConnection dbConnection = DbConnection.getDatabaseConnection();

            User usr = new User(
                username.getText(),
                email.getText(),
                firstName.getText(),
                lastName.getText(),
                password.getText(),
                "user"
            );

            boolean isRegistered = dbConnection.addUser(usr);

            if (isRegistered) {
                this.clearForm();
                AlertHelper.showSuccessAlert("You have registered successfully.");
            } else {
                AlertHelper.showErrorAlert("Something went wrong.");
            }
        }
    }

    private boolean isValidated() {
        window = registerButton.getScene().getWindow();
        DbConnection dbConnection = DbConnection.getDatabaseConnection();

        if (firstName.getText().equals("")) {
            AlertHelper.showErrorAlert("First name text field cannot be blank.");
            firstName.requestFocus();
        } else if (firstName.getText().length() < 2 || firstName.getText().length() > 25) {
            AlertHelper.showErrorAlert("First name text field cannot be less than 2 and greator than 25 characters.");
            firstName.requestFocus();
        } else if (lastName.getText().equals("")) {
            AlertHelper.showErrorAlert("Last name text field cannot be blank.");
            lastName.requestFocus();
        } else if (lastName.getText().length() < 2 || lastName.getText().length() > 25) {
            AlertHelper.showErrorAlert("Last name text field cannot be less than 2 and greator than 25 characters.");
            lastName.requestFocus();
        } else if (email.getText().equals("")) {
            AlertHelper.showErrorAlert("Email text field cannot be blank.");
            email.requestFocus();
        } else if (email.getText().length() < 5 || email.getText().length() > 45) {
            AlertHelper.showErrorAlert("Email text field cannot be less than 5 and greator than 45 characters.");
            email.requestFocus();
        } else if (username.getText().equals("")) {
            AlertHelper.showErrorAlert("Username text field cannot be blank.");
            username.requestFocus();
        } else if (username.getText().length() < 4 || username.getText().length() > 25) {
            AlertHelper.showErrorAlert("Username text field cannot be less than 4 and greator than 25 characters.");
            username.requestFocus();
        } else if (password.getText().equals("")) {
            AlertHelper.showErrorAlert("Password text field cannot be blank.");
            password.requestFocus();
        } else if (password.getText().length() < 4 || password.getText().length() > 50) {
            AlertHelper.showErrorAlert("Password text field cannot be less than 4 and greator than 50 characters.");
            password.requestFocus();
        } else if (confirmPassword.getText().equals("")) {
            AlertHelper.showErrorAlert("Confirm password text field cannot be blank.");
            confirmPassword.requestFocus();
        } else if (confirmPassword.getText().length() < 4 || password.getText().length() > 50) {
            AlertHelper.showErrorAlert("Confirm password text field cannot be less than 4 and greator than 50 characters.");
            confirmPassword.requestFocus();
        } else if (!password.getText().equals(confirmPassword.getText())) {
            AlertHelper.showErrorAlert("Password and confirm password text fields does not match.");
            password.requestFocus();
        } else if (dbConnection.isUsernameExists(username.getText())) {
            AlertHelper.showErrorAlert("The username is already taken by someone else.");
            username.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    private boolean clearForm() {
        firstName.clear();
        lastName.clear();
        email.clear();
        username.clear();
        password.clear();
        confirmPassword.clear();
        return true;
    }

    @FXML
    private void showLoginStage() throws IOException {
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("/com/shop/auth/LoginView.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("User Login");
        stage.show();
    }
}
