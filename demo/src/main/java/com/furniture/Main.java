package com.furniture;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/HomePageView.fxml")
        );

        Scene scene = new Scene(loader.load(), 1200, 750);

        scene.getStylesheets().add(
                getClass().getResource("/style/HomePage.css").toExternalForm()
        );

        stage.setTitle("Furniture Showroom Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}