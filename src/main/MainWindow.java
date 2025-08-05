package main;

import javafx.animation.FadeTransition;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.util.Duration;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;

import javafx.scene.effect.GaussianBlur;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import controller.DecorativeImage;
import controller.DrumAnimationView;
import controller.EventHandlerManager;
import controller.MusicManager;
import model.Drum;
import model.Card;

public class MainWindow {

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
	private MusicManager musicManager = new MusicManager();

	private StackPane appContainer;
	private StackPane gameEndModal;
	private StackPane screenContainer;
	private Label ballLabel;
	private Button welcomeMuteButton;
	private Button gameMuteButton;
	private Button generateCard;
	private Button startButton;
	private Button resetButton;
	private Button stopButton;
	private DecorativeImage imageManager;

	private ImageView welcomeSoundIcon, welcomeMuteIcon, gameSoundIcon, gameMuteIcon;

	private Drum drum = new Drum();

	public void start(Stage stage) {

		stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo_Icon.png")));

		createMuteButtons();
		createWelcomePane();
		createHistoryGrid();
		createLastBallsDisplay();
		createGamePane();
		musicManager.playWelcomeMusic();

		appContainer = new StackPane();
		appContainer.getStyleClass().add("app-container");

		screenContainer = new StackPane();
		screenContainer.getStyleClass().add("screen-container");

		createGameEndModal();

		EventHandlerManager handler = new EventHandlerManager(this, drum, ballLabel, welcomePane, gamePane, startButton,
				stopButton, resetButton, lastBallsLabels, ballLabelsMap, lastBallsBox, historyGrid);
		handler.configureAll();
		handler.initializeAudio();

		screenContainer.getChildren().addAll(welcomePane, gamePane);

		appContainer.getChildren().addAll(screenContainer, gameEndModal);

		imageManager = new DecorativeImage(appContainer);
		imageManager.loadDecorativeImages("/Images/LeftImage.png", "/Images/RightImage.png");
		imageManager.showForWelcomeScreen();

		appContainer.setPrefSize(1400, 900);
		appContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		Scene scene = new Scene(appContainer, 1400, 900);
		Font.loadFont(getClass().getResource("/Fonts/Coiny-Regular.ttf").toExternalForm(), 12);
		scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());

		stage.setScene(scene);
		stage.setTitle("Bongi Game");
		stage.show();
	}

	private void createWelcomePane() {

		DrumAnimationView animatedTitle = new DrumAnimationView();

		generateCard = new Button("GENERAR CARTONES");
		generateCard.setOnAction(e -> dialogCard());
		generateCard.getStyleClass().add("main-buttons");

		startButton = new Button("COMENZAR JUEGO");
		startButton.getStyleClass().add("main-buttons");

		HBox buttons = new HBox(10);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().addAll(generateCard, startButton);

		Button attributionButton = new Button();
		attributionButton.getStyleClass().add("attribution-button");
		attributionButton.setPrefSize(50, 50);
		attributionButton.setMaxSize(50, 50);
		attributionButton.setMinSize(50, 50);
		attributionButton.setOnAction(e -> showAttributions());

		HBox bottomButtons = new HBox(10, welcomeMuteButton, attributionButton);
		bottomButtons.setAlignment(Pos.CENTER);
		bottomButtons.setMaxWidth(120);

		VBox centerContent = new VBox(20, animatedTitle, buttons, bottomButtons);
		centerContent.setAlignment(Pos.CENTER);

		BorderPane layout = new BorderPane();
		layout.setCenter(centerContent);

		welcomePane = new VBox(layout);
		welcomePane.setAlignment(Pos.CENTER);
	}

	private void dialogCard() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Generar Bongi-Cartones");
		dialog.setHeaderText("¿Cuantos cartones quieres?");
		dialog.setContentText("Cantidad:");

		applyDialogStyle(dialog.getDialogPane());

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(input -> {
			try {
				int count = Integer.parseInt(input);
				if (count > 0) {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Guardar Bongi-Cartones");
					fileChooser.setInitialFileName("Bongi-Cartones" + count + ".pdf");
					fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

					File file = fileChooser.showSaveDialog(null);

					if (file != null) {
						Card.generateAndExportCards(count, file.getAbsolutePath());
						showSuccess("Generado correctamente " + count + " Bongi-Cartones ");
					} else {
						showInfo("Generación de Bongi-Cartones cancelada.");
					}
				} else {
					showError("Ingresa un número mayor a cero...");
				}
			} catch (NumberFormatException e) {
				showError("Ingresa un número válido!");
			} catch (Exception e) {
				showError("Error generando Bongi-Cartones: " + e.getMessage());
			}
		});
	}

	private void applyDialogStyle(DialogPane dialogPane) {
		dialogPane.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
		dialogPane.getStyleClass().add("dialog-pane");
	}

	private void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		applyDialogStyle(alert.getDialogPane());
		alert.showAndWait();
	}

	private void showInfo(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		applyDialogStyle(alert.getDialogPane());
		alert.showAndWait();
	}

	private void showError(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);
		applyDialogStyle(alert.getDialogPane());
		alert.showAndWait();
	}

	public void showWelcomeScreen() {
		welcomePane.setVisible(true);
		gamePane.setVisible(false);
		imageManager.showForWelcomeScreen();
	}

	public void showGameScreen() {
		welcomePane.setVisible(false);
		gamePane.setVisible(true);
		imageManager.hideForGameScreen();
	}

	public DecorativeImage getImageManager() {
		return imageManager;
	}

	private void createGamePane() {

		ballLabel = new Label("Waiting...");
		ballLabel.getStyleClass().add("main-ball-label");

		stopButton = new Button("PARAR");
		resetButton = new Button("VOLVER");

		stopButton.setVisible(false);
		stopButton.getStyleClass().add("stop-button");
		resetButton.setDisable(true);
		resetButton.getStyleClass().add("reset-button");

		VBox topLeft = new VBox();
		topLeft.getChildren().add(lastBallsBox);
		topLeft.setAlignment(Pos.TOP_LEFT);
		topLeft.setPadding(new Insets(20, 0, 0, 20));

		HBox topRight = new HBox(10);
		topRight.getChildren().addAll(resetButton, gameMuteButton);
		topRight.setAlignment(Pos.TOP_RIGHT);
		topRight.setPadding(new Insets(20, 20, 0, 0));

		BorderPane topSection = new BorderPane();
		topSection.setLeft(topLeft);
		topSection.setRight(topRight);

		VBox centerSection = new VBox(50);
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

		Label gameEndLabel = new Label("¡Juego Completado!");
		gameEndLabel.getStyleClass().add("game-end-title");

		Button newGameButton = new Button("NUEVO JUEGO");
		newGameButton.getStyleClass().add("new-game-button");
		newGameButton.setMinWidth(200);
		newGameButton.setMinHeight(50);

		newGameButton.setOnAction(e -> {
			hideGameEndModal();
			resetButton.fire();
		});

		VBox modalContent = new VBox(50);
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

		musicManager.stopMusic();

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
		javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(Duration.millis(400),
				gameEndModal);
		scaleIn.setFromX(0.8);
		scaleIn.setFromY(0.8);
		scaleIn.setToX(1.0);
		scaleIn.setToY(1.0);
		scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

		javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition(fadeIn,
				scaleIn);
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

	private void createMuteButtons() {
		Image soundImage = new Image(getClass().getResourceAsStream("/icons/sound.png"));
		Image muteImage = new Image(getClass().getResourceAsStream("/icons/mute.png"));

		ImageView welcomeSoundIcon = new ImageView(soundImage);
		ImageView welcomeMuteIcon = new ImageView(muteImage);
		ImageView gameSoundIcon = new ImageView(soundImage);
		ImageView gameMuteIconImg = new ImageView(muteImage);

		welcomeSoundIcon.setFitWidth(24);
		welcomeSoundIcon.setFitHeight(24);
		welcomeMuteIcon.setFitWidth(24);
		welcomeMuteIcon.setFitHeight(24);
		gameSoundIcon.setFitWidth(24);
		gameSoundIcon.setFitHeight(24);
		gameMuteIconImg.setFitWidth(24);
		gameMuteIconImg.setFitHeight(24);

		this.welcomeSoundIcon = welcomeSoundIcon;
		this.welcomeMuteIcon = welcomeMuteIcon;
		this.gameSoundIcon = gameSoundIcon;
		this.gameMuteIcon = gameMuteIconImg;

		welcomeMuteButton = new Button();
		welcomeMuteButton.getStyleClass().add("mute-button-glass");
		welcomeMuteButton.setOnAction(e -> handleMuteToggle());

		gameMuteButton = new Button();
		gameMuteButton.getStyleClass().add("mute-button-glass");
		gameMuteButton.setOnAction(e -> handleMuteToggle());

		updateMuteButtonsText();
	}

	private void handleMuteToggle() {
		musicManager.toggleMute();
		updateMuteButtonsText();
	}

	private void updateMuteButtonsText() {
		if (musicManager.isMuted()) {
			welcomeMuteButton.setGraphic(welcomeMuteIcon);
			gameMuteButton.setGraphic(gameMuteIcon);
		} else {
			welcomeMuteButton.setGraphic(welcomeSoundIcon);
			gameMuteButton.setGraphic(gameSoundIcon);
		}
	}

	private void showAttributions() {
		Alert attributionAlert = new Alert(Alert.AlertType.INFORMATION);
		attributionAlert.setTitle("Creditos & Atribuciones");
		attributionAlert.setHeaderText("Bongi Game - Creditos");

		String attributions = """

				🎵 Música:
				• "George Street Shuffle" by Kevin MacLeod
				   Licencia: Creative Commons Attribution 4.0
				   Fuente: http://incompetech.com/music/royalty-free/
				   Artista: http://incompetech.com/

				🎨 Imágenes:
				•Palm Tree Silhouettes by Vecteezy
				 Fuente: https://es.vecteezy.com/png-gratis/palmera-silueta

				🔤 Fuente:
				• Coiny Font: Open Font License

				📚 Librerias:
				• JavaFX: Oracle - Open Source License

				♥ Agradecimiento especial:
				¡Hecho con amor para los entusiastas del Bingo!

				Version: 1.0
				Developer: Julia Naranjo Gimena

				""";
		TextArea textArea = new TextArea(attributions);
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setFocusTraversable(false);
		textArea.setPrefSize(500, 300);
		textArea.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

		attributionAlert.getDialogPane().setContent(textArea);

		DialogPane dialogPane = attributionAlert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
		dialogPane.getStyleClass().add("dialog-pane");

		dialogPane.setMinWidth(550);
		dialogPane.setMinHeight(400);

		attributionAlert.showAndWait();
	}

	public MusicManager getMusicManager() {
		return musicManager;
	}

	private int getRow(int ballNumber) {
		return (ballNumber - 1) / COLUMNS;
	}

	private int getColumn(int ballNumber) {
		return (ballNumber - 1) % COLUMNS;
	}
}
