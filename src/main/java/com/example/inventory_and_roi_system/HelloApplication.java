package com.example.inventory_and_roi_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//project entry point

public class HelloApplication extends Application {
    //Sets up the Stage and Scene (window and content in the window)
    @Override
    public void start(Stage stage) throws IOException {
        //link java code to XML UI design file(hello-view.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        //sets dimension of scene
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);

        //configures window
        stage.setTitle("ROI Tracker");
        stage.setScene(scene);
        stage.show();
    }

    //call when application is closing
    @Override
    public void stop() {
        // Ensures the multi-threading scheduler stops when window closed
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}