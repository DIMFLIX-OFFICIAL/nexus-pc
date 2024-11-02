module com.shop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires javafx.graphics;

    opens com.shop to javafx.fxml;
    exports com.shop;
    exports com.shop.controllers;
    opens com.shop.controllers to javafx.fxml;
    exports com.shop.controllers.main_pages;
    opens com.shop.controllers.main_pages to javafx.fxml;

}
