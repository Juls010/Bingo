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
	
	
	
	@Override
	public void start(Stage primaryStage) {
		
		createWelcomePane();
		createHistoryGrid();
		createLastBallsDisplay();
		createGamePane();
		EventHandlerManager handler = new EventHandlerManager(this,drum, ballLabel, welcomePane, gamePane, startButton, stopButton, resetButton,
				lastBallsLabels, ballLabelsMap, lastBallsBox, historyGrid);
		handler.configureAll();

		VBox root = new VBox();
		root.getChildren().addAll(welcomePane, gamePane);
		
		Scene scene = new Scene(root, 1400, 900);
		scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.setTitle("Bingo Machine");
		primaryStage.show();	
	}
	
	private void createWelcomePane() {
		
		Label welcomeLabel = new Label("Welcome to Bingo!");
		welcomeLabel.setFont(new Font("Arial", 36));
		welcomeLabel.getStyleClass().add("welcome-label");
		
		startButton = new Button("Start");
		startButton.setFont(new Font(18));
		
		welcomePane = new VBox(20, welcomeLabel, startButton);
		welcomePane.setAlignment(javafx.geometry.Pos.CENTER);
		welcomePane.getStyleClass().add("welcome-label");
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
		gamePane.getStyleClass().add("game-pane");
		gamePane.setVisible(false);
	}
	

	private void createHistoryGrid() {
		historyGrid = new GridPane();
		historyGrid.setHgap(GAP_SIZE);
		historyGrid.setVgap(GAP_SIZE);
		historyGrid.setAlignment(Pos.CENTER);
		historyGrid.getStyleClass().add("history-grid");
		
		
		
		for(int i = 1; i <= TOTAL_BALLS; i++) {
			Label ballLabel = new Label(String.valueOf(i));
			ballLabel.setMinSize(BALL_SIZE, BALL_SIZE);
			ballLabel.getStyleClass().add("history-ball-default");
			ballLabelsMap.put(i, ballLabel);
			
			int col = getColumn(i);
			int row = getRow(i);
			historyGrid.add(ballLabel, col, row);
			
		}
	}
	
	public void markBallAsDrawn(int ballNumber) {
		Label label = ballLabelsMap.get(ballNumber);
		if (label != null) {
			label.getStyleClass().clear();
			label.getStyleClass().add("history-ball-marked");
		}
	}
	
	
	private void createLastBallsDisplay() {
		lastBallsBox = new HBox(10);
		lastBallsBox.setAlignment(Pos.TOP_LEFT);
		lastBallsBox.setPadding(new Insets(10, 0, 0, 10));
		
		for (int i = 0; i < LAST_BALLS_DISPLAY_COUNT; i++) {
			Label label = new Label("-");
			label.setMinSize(50, 50);
			label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			label.getStyleClass().add("last-ball-label");
			lastBallsLabels.add(label);
			lastBallsBox.getChildren().add(label);
		}
		
	}
	
	
	private int getRow(int ballNumber) {
		return (ballNumber - 1) / COLUMNS;
	}

	
	private int getColumn(int ballNumber) {
		return (ballNumber - 1) % COLUMNS;
	}

	
	public static void main (String [] args) {
		launch(args);
	}
}
