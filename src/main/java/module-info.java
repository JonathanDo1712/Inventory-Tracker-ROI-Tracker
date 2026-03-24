module com.example.inventory_and_roi_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.inventory_and_roi_system to javafx.fxml;
    exports com.example.inventory_and_roi_system;
    opens com.example.inventory_and_roi_system.models to javafx.base, javafx.fxml;
    exports com.example.inventory_and_roi_system.controllers;
    opens com.example.inventory_and_roi_system.controllers to javafx.fxml;
    exports com.example.inventory_and_roi_system.models;

}