package controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.MainWindow;
import model.Drum;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class EventHandlerManager {

	private final MainWindow mainWindow;
	private final Drum drum;
	private final Label ballLabel;
	private final VBox welcomePane;

	private final BorderPane gamePane;
	private final Button startButton;
	private final Button stopButton;
	private final Button resetButton;
	private final List<Label> lastBallsLabels;
	private final Map<Integer, Label> ballLabelsMap;
	private final Map<Integer, Media> audioMap = new HashMap<>();
	private MediaPlayer mediaPlayer;

	private PauseTransition pause;

	public EventHandlerManager(MainWindow mainWindow, Drum drum, Label ballLabel, VBox welcomePane, BorderPane gamePane,
			Button startButton, Button stopButton, Button resetButton, List<Label> lastBallsLabels,
			Map<Integer, Label> ballLabelsMap, HBox lastBallsBox, GridPane historyGrid) {
		this.mainWindow = mainWindow;
		this.drum = drum;
		this.ballLabel = ballLabel;
		this.welcomePane = welcomePane;
		this.gamePane = gamePane;
		this.startButton = startButton;
		this.stopButton = stopButton;
		this.resetButton = resetButton;
		this.lastBallsLabels = lastBallsLabels;
		this.ballLabelsMap = ballLabelsMap;
	}

	public void configureAll() {
		configureStartButton();
		configureStopButton();
		configureResetButton();
	}

	private void configureStartButton() {
		
		startButton.setOnAction(e -> {
			Node leftPalm = mainWindow.getImageManager().getLeftImage();
			Node rightPalm = mainWindow.getImageManager().getRightImage();

			FadeTransition fadeWelcome = new FadeTransition(Duration.seconds(0.8), welcomePane);
			fadeWelcome.setFromValue(1.0);
			fadeWelcome.setToValue(0.0);

			FadeTransition fadeLeftPalm = new FadeTransition(Duration.seconds(0.8), leftPalm);
			fadeLeftPalm.setFromValue(1.0);
			fadeLeftPalm.setToValue(0.0);

			FadeTransition fadeRightPalm = new FadeTransition(Duration.seconds(0.8), rightPalm);
			fadeRightPalm.setFromValue(1.0);
			fadeRightPalm.setToValue(0.0);

			ParallelTransition allFadeOut = new ParallelTransition(fadeWelcome, fadeLeftPalm, fadeRightPalm);

			allFadeOut.setOnFinished(event -> {
				welcomePane.setVisible(false);
				gamePane.setVisible(true);
				stopButton.setVisible(true);
				resetButton.setDisable(false);
				drawNextBall();
				mainWindow.getImageManager().hideForGameScreen();

				mainWindow.getMusicManager().stopMusic();
				mainWindow.getMusicManager().playGameMusic();

			});

			allFadeOut.play();
		});

	}

	private void configureStopButton() {
		stopButton.setOnAction(e -> {
			if (pause != null && pause.getStatus() == javafx.animation.Animation.Status.RUNNING) {
				pause.stop();
				stopButton.setText("REANUDAR");
			} else {
				drawNextBall();
				stopButton.setText("PARAR");
			}
		});
	}

	private void configureResetButton() {
		resetButton.setOnAction(e -> {
			drum.reset();
			ballLabel.setText("Esperando...");
			stopDrawing();

			stopButton.setVisible(false);
			stopButton.setText("PARARs");
			resetButton.setDisable(true);

			gamePane.setVisible(false);

			ImageView leftPalm = mainWindow.getImageManager().getLeftImage();
			ImageView rightPalm = mainWindow.getImageManager().getRightImage();

			leftPalm.setOpacity(0.0);
			rightPalm.setOpacity(0.0);

			mainWindow.getImageManager().showForWelcomeScreen();

			FadeTransition leftFadeIn = new FadeTransition(Duration.seconds(0.5), leftPalm);
			leftFadeIn.setFromValue(0.0);
			leftFadeIn.setToValue(1.0);

			FadeTransition rightFadeIn = new FadeTransition(Duration.seconds(0.5), rightPalm);
			rightFadeIn.setFromValue(0.0);
			rightFadeIn.setToValue(1.0);

			leftFadeIn.play();
			rightFadeIn.play();

			welcomePane.setVisible(true);
			welcomePane.setOpacity(0.0);

			FadeTransition welcomeFadeIn = new FadeTransition(Duration.seconds(0.8), welcomePane);
			welcomeFadeIn.setFromValue(0.0);
			welcomeFadeIn.setToValue(1.0);
			welcomeFadeIn.play();

			startButton.setVisible(true);
			mainWindow.getMusicManager().stopMusic();
			mainWindow.getMusicManager().playWelcomeMusic();

			ballLabelsMap.values().forEach(label -> {
				label.getStyleClass().setAll("history-ball-default");
			});

			lastBallsLabels.forEach(label -> {
				label.setText("-");
				label.getStyleClass().setAll("last-ball-label");
			});
		});
	}

	private void drawNextBall() {

		if (!drum.hasBallsLeft()) {
			mainWindow.showGameEndModal();
			return;
		}
		
		Integer ball = drum.drawBall();
		playNumberAudio(ball);
		
		ballLabel.setText(String.valueOf(ball));
		mainWindow.markBallAsDrawn(ball);
		

		List<Integer> lastBalls = drum.getLastBalls(3);

		for (int i = 0; i < lastBallsLabels.size(); i++) {
			Label displayLabel = lastBallsLabels.get(i);

			if (i < lastBalls.size()) {
				int ballValue = lastBalls.get(lastBalls.size() - 1 - i);
				displayLabel.setText(String.valueOf(ballValue));

				if (i == 0) {
					displayLabel.getStyleClass().setAll("last-ball-label", "last-ball-active");
				} else {
					displayLabel.getStyleClass().setAll("last-ball-label");
				}

			} else {
				displayLabel.setText("-");
				displayLabel.getStyleClass().setAll("last-ball-label");
			}
		}

		pause = new PauseTransition(Duration.seconds(4));
		pause.setOnFinished(e -> drawNextBall());
		pause.play();

	}
	
	private void preloadAudioFiles() {
		for (int i = 1; i <= 90; i++) {
			String path = "/audio/" + i + ".mp3";
			Media media = new Media(getClass().getResource(path).toExternalForm());
			audioMap.put(i, media);
		}
	}

	
	private void playNumberAudio(int number) {
		if (mediaPlayer != null) {
	        mediaPlayer.stop();
	        mediaPlayer.dispose();
	    }
	    Media media = audioMap.get(number);
	    if (media != null) {
	        mediaPlayer = new MediaPlayer(media);
	        mediaPlayer.play();
	    }
	}
	
	public void initializeAudio() {
	    preloadAudioFiles();
	}

	private void stopDrawing() {
		if (pause != null) {
			pause.stop();
		}
	}
	
}
