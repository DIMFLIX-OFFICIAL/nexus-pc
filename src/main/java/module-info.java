module com.shop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.base;

    opens com.shop.database to javafx.fxml;
    exports com.shop;
    exports com.shop.controllers;
    opens com.shop.controllers to javafx.fxml;
    exports com.shop.controllers.main_pages;
    opens com.shop.controllers.main_pages to javafx.fxml;
    opens com.shop.controllers.main_pages.childrens;
    exports com.shop.controllers.auth;
    opens com.shop.controllers.auth to javafx.fxml;
    exports com.shop.controllers.admin;
    opens com.shop.controllers.admin to javafx.fxml;
    exports com.shop.database.models;
    opens com.shop.database.models to javafx.base;
}
