package controller;


import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.InputStream;


public class SplashScreen extends Application {

	@Override
	public void start(Stage splashStage) {
	StackPane root = new StackPane();
	root.setStyle("-fx-background-color: black;");
	
	Image logo = new Image(getClass().getResourceAsStream("/logo.png"));
	ImageView logoView = new ImageView(logo);
	logoView.setFitWidth(250);
	logoView.setPreserveRatio(true);
	
	root.getChildren().add(logoView);
	
	Scene scene = new Scene(root, 800, 500);
	scene.setFill(Color.BLACK);
	splashStage.initStyle(StageStyle.UNDECORATED);
	splashStage.setScene(scene);
	splashStage.show();
	
	PauseTransition delay = new PauseTransition(Duration.seconds(3));
	delay.setOnFinished(event -> {
		splashStage.close();
		
		MainWindow mainWindow = new MainWindow();
		try {
			mainWindow.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	});
	delay.play();
	
	}
	
	public static void main(String [] args) {
		launch(args);
	}
}
