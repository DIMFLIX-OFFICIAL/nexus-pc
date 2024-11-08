package com.shop.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class AlertHelper {

    public static boolean result = false;

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        if (alertType.name() == null ? AlertType.INFORMATION.name() == null : alertType.name().equals(AlertType.INFORMATION.name())) {
            Notifications.create()
                    .darkStyle()
                    .title(title)
                    .text(message).hideAfter(Duration.seconds(10))
                    .showInformation();
        } else if (alertType.name() == null ? Alert.AlertType.ERROR.name() == null : alertType.name().equals(Alert.AlertType.ERROR.name())) {
            Notifications.create()
                    .darkStyle()
                    .title(title)
                    .text(message).hideAfter(Duration.seconds(10))
                    .showError();
        }
    }

    public static void showErrorAlert(String message) {
        AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    public static void showSuccessAlert(String message) {
        AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, "Success", message);
    }
}