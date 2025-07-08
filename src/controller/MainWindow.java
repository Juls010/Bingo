package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Drum;

public class MainWindow extends Application {

	private static final int TOTAL_BALLS = 90;
	private static final int COLUMNS = 15;
	private static final int BALL_SIZE = 45;
	private static final int GAP_SIZE = 10;
	private static final int LAST_BALLS_DISPLAY_COUNT = 3;

	private GridPane historyGrid;
	private Map<Integer, Label> ballLabelsMap = new HashMap();
	private VBox welcomePane;
	private BorderPane gamePane;
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
		EventHandlerManager handler = new EventHandlerManager(this, drum, ballLabel, welcomePane, gamePane, startButton,
				stopButton, resetButton, lastBallsLabels, ballLabelsMap, lastBallsBox, historyGrid);
		handler.configureAll();

		// NUEVO: Contenedor principal con fondo
	    StackPane appContainer = new StackPane();
	    appContainer.getStyleClass().add("app-container");
	    
	    // NUEVO: Contenedor de las pantallas
	    StackPane screenContainer = new StackPane();
	    screenContainer.getStyleClass().add("screen-container");
	    
	    // Agregar los paneles al contenedor de pantallas
	    screenContainer.getChildren().addAll(welcomePane, gamePane);
	    
	    // Agregar el contenedor de pantallas al contenedor principal
	    appContainer.getChildren().add(screenContainer);

	    appContainer.setPrefSize(1400, 900);
	    appContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

	    Scene scene = new Scene(appContainer, 1400, 900);
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
		startButton.getStyleClass().add("primary-button");

		welcomePane = new VBox(20, welcomeLabel, startButton);
		welcomePane.setAlignment(javafx.geometry.Pos.CENTER);
		welcomePane.getStyleClass().add("welcome-section");
	}

	private void createGamePane() {

		ballLabel = new Label("Waiting...");
		ballLabel.getStyleClass().add("main-ball-label");

		stopButton = new Button("Stop");
		resetButton = new Button("Reset");

		stopButton.setVisible(false);
		stopButton.getStyleClass().add("stop-button");
		resetButton.setDisable(true);
		resetButton.getStyleClass().add("reset-button");

		VBox topLeft = new VBox();
		topLeft.getChildren().add(lastBallsBox);
		topLeft.setAlignment(Pos.TOP_LEFT);
		topLeft.setPadding(new Insets(20, 0, 0, 20));

		VBox topRight = new VBox();
		topRight.getChildren().add(resetButton);
		topRight.setAlignment(Pos.TOP_RIGHT);
		topRight.setPadding(new Insets(20, 20, 0, 0));

		BorderPane topSection = new BorderPane();
		topSection.setLeft(topLeft);
		topSection.setRight(topRight);

		VBox centerSection = new VBox(30);
		centerSection.getChildren().addAll(ballLabel, stopButton);
		centerSection.setAlignment(Pos.CENTER);

		VBox bottomSection = new VBox();
		bottomSection.getChildren().add(historyGrid);
		bottomSection.setAlignment(Pos.CENTER);
		bottomSection.setPadding(new Insets(20, 0, 20, 0));

		BorderPane mainLayout = new BorderPane();
		mainLayout.setTop(topSection);
		mainLayout.setCenter(centerSection);
		mainLayout.setBottom(bottomSection);

		mainLayout.setPrefSize(1400, 900);
		mainLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		mainLayout.getStyleClass().add("game-section");
		mainLayout.setVisible(false);

		gamePane = mainLayout;

	}
	
	private void createHistoryGrid() {
		historyGrid = new GridPane();
		historyGrid.setHgap(GAP_SIZE);
		historyGrid.setVgap(GAP_SIZE);
		historyGrid.setAlignment(Pos.CENTER);
		historyGrid.getStyleClass().add("history-grid");

		for (int i = 1; i <= TOTAL_BALLS; i++) {
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
		lastBallsBox.getStyleClass().add("last-balls-container");
		
		for (int i = 0; i < LAST_BALLS_DISPLAY_COUNT; i++) {
			Label label = new Label("-");
			label.setMinSize(40, 40);
			label.setPrefSize(40, 40);
			label.setMaxSize(40, 40);
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

	public static void main(String[] args) {
		launch(args);
	}
}
