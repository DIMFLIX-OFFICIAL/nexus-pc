package com.shop.controllers.auth;

import com.shop.database.DbConnection;
import com.shop.helper.AlertHelper;
import com.shop.controllers.SharedData;
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


public class LoginController implements Initializable {
    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button loginButton;

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
    private void login() throws Exception {
        if (this.isValidated()) {
            String usernameText = username.getText();
            String passwordText = password.getText();

            if (DbConnection.getDatabaseConnection().authenticateUser(usernameText, passwordText)) {
                User user = DbConnection.getDatabaseConnection().getUserByUsername(usernameText);
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                SharedData.setAuthenticatedUser(user);

                Parent root = FXMLLoader.load(getClass().getResource("/com/shop/MainPanelView.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Admin Panel");
                stage.show();
            } else {
                AlertHelper.showErrorAlert("Invalid username and password.");
                username.requestFocus();
            }
        }
    }


    private boolean isValidated() {

        window = loginButton.getScene().getWindow();
        if (username.getText().equals("")) {
            AlertHelper.showErrorAlert("Username text field cannot be blank.");
            username.requestFocus();
        } else if (username.getText().length() < 5 || username.getText().length() > 25) {
            AlertHelper.showErrorAlert("Username text field cannot be less than 5 and greator than 25 characters.");
            username.requestFocus();
        } else if (password.getText().equals("")) {
            AlertHelper.showErrorAlert("Password text field cannot be blank.");
            password.requestFocus();
        } else if (password.getText().length() < 4 || password.getText().length() > 50) {
            AlertHelper.showErrorAlert("Password text field cannot be less than 4 and greator than 50 characters.");
            password.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    @FXML
    private void showRegisterStage() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("/com/shop/auth/RegisterView.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("User Registration");
        stage.show();
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
