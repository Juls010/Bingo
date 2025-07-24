package controller;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Drum;
import model.Card;
public class MainWindow extends Application {

	private static final int TOTAL_BALLS = 90;
	private static final int COLUMNS = 15;
	private static final int BALL_SIZE = 45;
	private static final int GAP_SIZE = 18;
	private static final int LAST_BALLS_DISPLAY_COUNT = 3;

	private GridPane historyGrid;
	private Map<Integer, Label> ballLabelsMap = new HashMap();
	private VBox welcomePane;
	private BorderPane gamePane;
	private HBox lastBallsBox;
	private List<Label> lastBallsLabels = new ArrayList<>();

	private StackPane gameEndModal;
	private StackPane screenContainer;
	private Label ballLabel;
	private Button generateCard;
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
		
		
	    StackPane appContainer = new StackPane();
	    appContainer.getStyleClass().add("app-container");
	    
	    
	    screenContainer = new StackPane();
	    screenContainer.getStyleClass().add("screen-container");
	    
	    createGameEndModal();
	    
		EventHandlerManager handler = new EventHandlerManager(this, drum, ballLabel, welcomePane, gamePane, startButton,
				stopButton, resetButton, lastBallsLabels, ballLabelsMap, lastBallsBox, historyGrid);
		handler.configureAll();
		
	    screenContainer.getChildren().addAll(welcomePane, gamePane);
	    
	    appContainer.getChildren().addAll(screenContainer,gameEndModal);

	    appContainer.setPrefSize(1400, 900);
	    appContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

	    Scene scene = new Scene(appContainer, 1400, 900);
	    Font.loadFont(getClass().getResource("/Fonts/Coiny-Regular.ttf").toExternalForm(), 12);
		scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Bongi Game");
		primaryStage.show();
	}

	private void createWelcomePane() {

		Label welcomeLabel = new Label("Welcome to Bingo!");
		welcomeLabel.setFont(new Font("Arial", 36));
		welcomeLabel.getStyleClass().add("welcome-label");

		generateCard =  new Button("GENERATE CARDS");
		generateCard.setOnAction(e -> dialogCard());
		generateCard.getStyleClass().add("primary-button");
		
		startButton = new Button("START GAME");
		startButton.setFont(new Font(18));
		startButton.getStyleClass().add("primary-button");

		welcomePane = new VBox(20, welcomeLabel, generateCard,startButton);
		welcomePane.setAlignment(javafx.geometry.Pos.CENTER);
		welcomePane.getStyleClass().add("welcome-section");
	}
	
	private void dialogCard() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("GENERATE CARDS");
		dialog.setHeaderText("¿HOW MANY CARDS WOULD YOU LIKE?");
		dialog.setContentText("ENTER A NUMBER");
		
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(input -> {
			try {
				int count = Integer.parseInt(input);
				if (count > 0 ) {
					List<Card> cards = Card.generateCard(count);
				} else {
					showError("ENTER A NUMBER GREATER THAN ZERO.");
				}
			}catch (NumberFormatException e){
				showError("PLEASE, NTER A VALID NUMBER");
			}
		});
	}
	
	private void showError(String error) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("ERROR");
		alert.setContentText(error);
		alert.showAndWait();
	}
	
	private void createGamePane() {

		ballLabel = new Label("Waiting...");
		ballLabel.getStyleClass().add("main-ball-label");

		stopButton = new Button("STOP");
		resetButton = new Button("RESET");

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
	
	private void createGameEndModal() {
		
		Rectangle overlay = new Rectangle();
		overlay.setFill(Color.rgb(0, 0, 0, 0.5)); 
		overlay.widthProperty().bind(screenContainer.widthProperty());
		overlay.heightProperty().bind(screenContainer.heightProperty());
		
		
		Label gameEndLabel = new Label("¡GAME COMPLETE!");
		gameEndLabel.getStyleClass().add("game-end-title");
		

		Button newGameButton = new Button("NEW GAME");
		newGameButton.getStyleClass().add("new-game-button");
		newGameButton.setMinWidth(200);
		newGameButton.setMinHeight(50);
		
		
		newGameButton.setOnAction(e -> {
			hideGameEndModal();
			resetButton.fire();
		});
		
		
		VBox modalContent = new VBox(25);
		modalContent.getChildren().addAll(gameEndLabel, newGameButton);
		modalContent.setAlignment(Pos.CENTER);
		modalContent.getStyleClass().add("game-end-modal-content");
		modalContent.setMaxWidth(550);
		modalContent.setMaxHeight(400);
		modalContent.setPadding(new Insets(40, 50, 50, 50));
		
		gameEndModal = new StackPane();
		gameEndModal.getChildren().addAll(overlay, modalContent);
		gameEndModal.setAlignment(Pos.CENTER);
		gameEndModal.setVisible(false); 
		gameEndModal.setManaged(false); 
	}
	
	public void showGameEndModal() {
		
		GaussianBlur blur = new GaussianBlur(10);
		screenContainer.setEffect(blur);
		
		
		gameEndModal.setVisible(true);
		gameEndModal.setManaged(true);
		gameEndModal.setOpacity(0);
		gameEndModal.setScaleX(0.8); 
		gameEndModal.setScaleY(0.8);
		
		FadeTransition fadeIn = new FadeTransition(Duration.millis(300), gameEndModal);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(Duration.millis(400), gameEndModal);
		scaleIn.setFromX(0.8);
		scaleIn.setFromY(0.8);
		scaleIn.setToX(1.0);
		scaleIn.setToY(1.0);
		scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
		
		
		javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(fadeIn, scaleIn);
		parallelTransition.play();
	}
	
	public void hideGameEndModal() {
		
		screenContainer.setEffect(null);
		
		
		FadeTransition fadeOut = new FadeTransition(Duration.millis(300), gameEndModal);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.setOnFinished(e -> {
			gameEndModal.setVisible(false);
			gameEndModal.setManaged(false);
		});
		fadeOut.play();
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
