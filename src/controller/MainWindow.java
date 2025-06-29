package controller;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import model.Drum;

public class MainWindow  extends Application {
	
	private static final int TOTAL_BALLS = 90;
	private static final int COLUMNS = 10;
	private static final int BALL_SIZE = 30;
	private static final int  GAP_SIZE = 5;
	private static final int LAST_BALLS_DISPLAY_COUNT = 3;
	
	private GridPane historyGrid;
	private Map<Integer, Label> ballLabelsMap = new HashMap();
	private VBox welcomePane;
	private VBox gamePane;
	private HBox lastBallsBox;
	private List<Label> lastBallsLabels = new ArrayList<>();
	
	private Label ballLabel;
	private Button startButton;
	private Button resetButton;
	private Button stopButton;
	
	private Drum drum = new Drum();
	
	private javafx.animation.PauseTransition pause;
	
	
	@Override
	public void start(Stage primaryStage) {
		
		createWelcomePane();
		createHistoryGrid();
		createLastBallsDisplay();
		createGamePane();
		configureEventHandlers();
		
		VBox root = new VBox();
		root.getChildren().addAll(welcomePane, gamePane);
		
		Scene scene = new Scene(root, 1400, 900);
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
		
		gamePane = new VBox(20, lastBallsBox,ballLabel, stopButton, resetButton, historyGrid);
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
		historyGrid.setHgap(GAP_SIZE);
		historyGrid.setVgap(GAP_SIZE);
		historyGrid.setAlignment(Pos.CENTER);
		historyGrid.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0;");
		
		
		
		for(int i = 1; i <= TOTAL_BALLS; i++) {
			Label ballLabel = new Label(String.valueOf(i));
			ballLabel.setMinSize(BALL_SIZE, BALL_SIZE);
			ballLabelsMap.put(i, ballLabel);
			
			int col = getColumn(i);
			int row = getRow(i);
			historyGrid.add(ballLabel, col, row);
			
		}
	}
	
	private void markBallAsDrawn(int ballNumber) {
		Label label = ballLabelsMap.get(ballNumber);
		if (label != null) {
			label.setStyle("-fx-border-color: black; -fx-alignment: center; -fx-background-color: lightgreen; -fx-font-weight: bold;");
		}
	}
	
	
	private void createLastBallsDisplay() {
		lastBallsBox = new HBox(10);
		lastBallsBox.setAlignment(Pos.TOP_LEFT);
		lastBallsBox.setPadding(new Insets(10, 0, 0, 10));
		
		for (int i = 0; i < LAST_BALLS_DISPLAY_COUNT; i++) {
			Label label = new Label("-");
			label.setMinSize(50, 50);
			label.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center;");
			label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			lastBallsLabels.add(label);
			lastBallsBox.getChildren().add(label);
		}
		
	}
	
	
	private void updateLastBallsDisplay() {
		List<Integer> lastBalls = drum.getLastBalls(LAST_BALLS_DISPLAY_COUNT);
		
		for (int i = 0; i < LAST_BALLS_DISPLAY_COUNT; i++) {
			Label label = lastBallsLabels.get(i);
			if(i < lastBalls.size()) {
				label.setText(String.valueOf(lastBalls.get(lastBalls.size()- 1 - i)));
				label.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightblue; -fx-alignment: center;");
			}else {
				label.setText("-");
				label.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center;");
			}
		}
	}
	
	
	private void drawNextBall() {
		
		if(!drum.hasBallsLeft()) {
			ballLabel.setText("¡Game Over! T-T");
			return;
		}
		
		Integer ball = drum.drawBall();
		ballLabel.setText("Ball: " + ball);
		markBallAsDrawn(ball);
		updateLastBallsDisplay();
		
		pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> drawNextBall());
		pause.play();
		
	}
	
	private int getRow(int ballNumber) {
		return (ballNumber - 1) / COLUMNS;
	}

	private int getColumn(int ballNumber) {
		return (ballNumber - 1) % COLUMNS;
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
