package controller;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import model.Drum;

public class MainWindow  extends Application {
	
	private GridPane historyGrid;
	private Map<Integer, Label> ballLabelsMap = new HashMap();
	private VBox welcomePane;
	private VBox gamePane;
	
	private Label ballLabel;
	private Button startButton;
	private Button resetButton;
	private Button stopButton;
	
	private Drum drum = new Drum();
	
	private javafx.animation.PauseTransition pause;
	
	
	@Override
	public void start(Stage primaryStage) {
		
		createWelcomePane();
		createGamePane();
		configureEventHandlers();
		
		VBox root = new VBox();
		root.getChildren().addAll(welcomePane, gamePane);
		
		Scene scene = new Scene(root, 800, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Bingo Machine");
		primaryStage.show();	
	}
	
	private void createWelcomePane() {
		
		Label welcomeLabel = new Label("Welcome to Bingo!");
		welcomeLabel.setFont(new Font("Arial", 36));
		welcomeLabel.setStyle("-fx-text-fill: #333;");
		
		startButton = new Button("Start");
		startButton.setFont(new Font(18));
		
		welcomePane = new VBox(20, welcomeLabel, startButton);
		welcomePane.setAlignment(javafx.geometry.Pos.CENTER);
		welcomePane.setStyle("-fx-background-color: white; -fx-padding: 40;");
	}
	
	private void createGamePane() {
		
		ballLabel = new Label ("Waiting...");
		ballLabel.setFont(new Font(32));
		
		stopButton = new Button("Stop");
		resetButton = new Button("Reset");
		
		stopButton.setVisible(false);
		resetButton.setDisable(true);
		
		gamePane = new VBox(20,ballLabel, stopButton, resetButton);
		gamePane.setAlignment(javafx.geometry.Pos.CENTER);
		gamePane.setStyle("-fx-background-color: white; -fx-padding: 40;");
		gamePane.setVisible(false);
	}
	
	
	private void configureEventHandlers() {
		
		startButton.setOnAction(e -> {
			FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), welcomePane);
			fadeOut.setFromValue(1.0);
			fadeOut.setToValue(0.0);
			fadeOut.setOnFinished(event -> {
				welcomePane.setVisible(false);
				gamePane.setVisible(true);
				stopButton.setVisible(true);
				resetButton.setDisable(false);
				drawNextBall();
			});
			fadeOut.play();
		});

		stopButton.setOnAction(e -> {
			if (pause != null && pause.getStatus() == javafx.animation.Animation.Status.RUNNING) {
				pause.stop();
				stopButton.setText("Start");
			} else {
				drawNextBall();
				stopButton.setText("Stop");
			}
		});

		resetButton.setOnAction(e -> {
			drum.reset();
			ballLabel.setText("Waiting...");
			stopDrawing();
			
			stopButton.setVisible(false);
			stopButton.setText("Stop");
			resetButton.setDisable(true);
			
			gamePane.setVisible(false);
			welcomePane.setVisible(true);
			welcomePane.setOpacity(1.0);
			startButton.setVisible(true);
			
		});
		
	}

	private void createHistoryGrid() {
		historyGrid = new GridPane();
		historyGrid.setHgap(5);
		historyGrid.setVgap(5);
		historyGrid.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
		
		int cols = 10;
		int rows = 9;
		
		for(int i = 1; i <= 90; i++) {
			Label ballLabel = new Label(String.valueOf(i));
			ballLabel.setMinSize(30, 30);
			ballLabelsMap.put(i, ballLabel);
			
			int col = (i - 1) % cols;
			int row = (i - 1) / cols;
			historyGrid.add(ballLabel, col, row);
			
		}
	}
	
	
	private void drawNextBall() {
		
		if(!drum.hasBallsLeft()) {
			ballLabel.setText("¡Game Over! T-T");
			return;
		}
		
		Integer ball = drum.drawBall();
		ballLabel.setText("Ball: " + ball);
		
		pause = new javafx.animation.PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> drawNextBall());
		pause.play();
		
	}
	
	private void stopDrawing() {
		if (pause != null) {
			pause.stop();
		}
	}
	
	public static void main (String [] args) {
		launch(args);
	}
}
