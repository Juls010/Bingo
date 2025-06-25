package controller;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import model.Drum;

public class MainWindow  extends Application {
	
	private Drum drum = new Drum();
	private Label ballLabel;
	private Button startButton;
	private Button resetButton;
	private PauseTransition pause;
	
	@Override
	public void start(Stage primaryStage) {
		ballLabel = new Label("Welcome to Bingo!");
		ballLabel.setFont(new Font(32));
		
		startButton = new Button("Start");
		resetButton = new Button("Resert");
		
		startButton.setOnAction(e -> {
			startButton.setDisable(true);
			resetButton.setDisable(false);
			drawNextBall();
		});
		
		resetButton.setOnAction(e -> {
			drum.reset();
			ballLabel.setText("Welcome to Bingo!");
		});
		
		VBox layout = new VBox(20);
		layout.getChildren().addAll(ballLabel, startButton, resetButton);
		layout.setAlignment(Pos.CENTER);
		layout.setStyle("-fx-padding: 40;");
		
		Scene scene = new Scene(layout, 400, 300);
		primaryStage.setTitle("Bingo Machine");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		resetButton.setDisable(true);	
	}
	
	private void drawNextBall() {
		
		if(!drum.hasBallsLeft()) {
			ballLabel.setText("Game Over... No balls left!");
			return;
		}
		
		Integer ball = drum.drawBall();
		ballLabel.setText("Ball " + ball);
		
		pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> drawNextBall());
		pause.play();
	}
	
	public static void main (String [] args) {
		launch(args);
	}
}
