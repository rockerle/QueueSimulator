package net.rockerle.simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 605);
        stage.setTitle("Queue Simulator");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }
    public static void main(String[] args) {
        launch();
    }
}