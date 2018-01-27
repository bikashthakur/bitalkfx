package bitalkclientfx;

/**
 * Created by BIKASH THAKUR on 10-Dec-18.
 */

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getResource("application.fxml"));
            Scene scene = new Scene(root, 600, 480);
            primaryStage.setTitle("BiTalk");
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(480);
            primaryStage.setMinWidth(600);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
