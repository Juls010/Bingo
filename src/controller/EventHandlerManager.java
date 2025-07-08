package controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Drum;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.Map;

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
	}

	private void configureStopButton() {
		stopButton.setOnAction(e -> {
			if (pause != null && pause.getStatus() == javafx.animation.Animation.Status.RUNNING) {
				pause.stop();
				stopButton.setText("Start");
			} else {
				drawNextBall();
				stopButton.setText("Stop");
			}
		});
	}

	private void configureResetButton() {
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
			ballLabel.setText("¡Game Over! T-T");
			return;
		}

		Integer ball = drum.drawBall(); 
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

		// Añade pausa para el modo automático (si aplica)
		pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(e -> drawNextBall());
		pause.play();

	}

	private void stopDrawing() {
		if (pause != null) {
			pause.stop();
		}
	}
}
