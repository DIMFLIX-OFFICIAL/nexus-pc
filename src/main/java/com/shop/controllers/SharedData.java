package com.shop.controllers;

import com.shop.database.models.Computer;
import com.shop.database.models.User;

public class SharedData {
    public static User authenticatedUser;
    public static MainPanelController controller;
    public static Computer selectedComputer;

    public static void setAuthenticatedUser(User value) {
        authenticatedUser = value;
    }

    public static User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static void setMainController(MainPanelController value) {
        controller = value;
    }

    public static MainPanelController getMainController() {
        return controller;
    }

    public static void setSelectedComputer(Computer value) {
        selectedComputer = value;
    }

    public static Computer getSelectedComputer() {
        return selectedComputer;
    }
}