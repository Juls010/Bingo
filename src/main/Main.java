package main;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label etiqueta = new Label("¡Hola, Bingo!");
        StackPane root = new StackPane();
        root.getChildren().add(etiqueta);
        
        Scene escena = new Scene(root, 400, 200);
        primaryStage.setTitle("Prueba JavaFX");
        primaryStage.setScene(escena);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

