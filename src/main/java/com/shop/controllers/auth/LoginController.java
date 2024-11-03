package com.shop.controllers.auth;

import com.shop.database.DbConnection;
import com.shop.helper.AlertHelper;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;


public class LoginController implements Initializable {

    private final Connection con;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button loginButton;

    Window window;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public LoginController() {
        DbConnection dbc = DbConnection.getDatabaseConnection();
        con = dbc.getConnection();
    }

    @FXML
    private void login() throws Exception {
        if (this.isValidated()) {
            String usernameText = username.getText();
            String passwordText = password.getText();

            if (DbConnection.getDatabaseConnection().authenticateUser(usernameText, passwordText)) {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                Parent root = FXMLLoader.load(getClass().getResource("/com/shop/MainPanelView.fxml"));
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.setTitle("Admin Panel");
                stage.show();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                        "Invalid username and password.");
                username.requestFocus();
            }
        }
    }


    private boolean isValidated() {

        window = loginButton.getScene().getWindow();
        if (username.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be blank.");
            username.requestFocus();
        } else if (username.getText().length() < 5 || username.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Username text field cannot be less than 5 and greator than 25 characters.");
            username.requestFocus();
        } else if (password.getText().equals("")) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Password text field cannot be blank.");
            password.requestFocus();
        } else if (password.getText().length() < 5 || password.getText().length() > 25) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, window, "Error",
                    "Password text field cannot be less than 5 and greator than 25 characters.");
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
}
