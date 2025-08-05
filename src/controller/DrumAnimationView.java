package controller;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class DrumAnimationView extends StackPane {

	private static final int DRUM_SIZE = 300;

	private static Font customFont;

	static {
		try {
			customFont = Font.loadFont(DrumAnimationView.class.getResourceAsStream("/fonts/Coiny-Regular.ttf"), 16);

			if (customFont == null) {
				customFont = Font.font("Arial", FontWeight.BOLD, 16);
			}
		} catch (Exception e) {
			customFont = Font.font("Arial", FontWeight.BOLD, 16);
		}
	}

	public DrumAnimationView() {
		this.setPrefSize(600, 500);
		this.setMaxSize(600, 500);
		this.setMinSize(600, 500);

		Pane drumContainer = new Pane();
		drumContainer.setPrefSize(DRUM_SIZE, DRUM_SIZE);
		drumContainer.setMaxSize(DRUM_SIZE, DRUM_SIZE);
		drumContainer.setMinSize(DRUM_SIZE, DRUM_SIZE);

		Circle drum = createDrum();
		drumContainer.getChildren().add(drum);

		createExactBalls(drumContainer);
		createExactSparkles(drumContainer);

		ImageView title = createTitle();

		this.getChildren().addAll(title, drumContainer);
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		title.setTranslateY(-180);
		StackPane.setAlignment(drumContainer, Pos.CENTER);
		drumContainer.setTranslateY(20);
	}

	private Circle createDrum() {
		Circle drum = new Circle(DRUM_SIZE / 2);

		RadialGradient drumGradient = new RadialGradient(0, 0, 0.3, 0.3, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, Color.web("#fafafa")), new Stop(0.4, Color.web("#f8f6f1")),
				new Stop(0.7, Color.web("#f0eae0")), new Stop(1, Color.web("#f1e4ca")));

		drum.setFill(drumGradient);
		drum.setStroke(Color.web("#fffeff"));
		drum.setStrokeWidth(3);

		DropShadow outerShadow = new DropShadow();
		outerShadow.setRadius(40);
		outerShadow.setOffsetY(25);
		outerShadow.setColor(Color.color(0, 0, 0, 0.4));

		InnerShadow innerShadowLight = new InnerShadow();
		innerShadowLight.setRadius(20);
		innerShadowLight.setOffsetY(-15);
		innerShadowLight.setColor(Color.color(1, 1, 1, 0.15));

		InnerShadow innerShadowDark = new InnerShadow();
		innerShadowDark.setRadius(20);
		innerShadowDark.setOffsetY(15);
		innerShadowDark.setColor(Color.color(0, 0, 0, 0.25));

		innerShadowLight.setInput(innerShadowDark);
		outerShadow.setInput(innerShadowLight);
		drum.setEffect(outerShadow);

		drum.setCenterX(DRUM_SIZE / 2);
		drum.setCenterY(DRUM_SIZE / 2);

		return drum;
	}

	private void createExactBalls(Pane container) {
		createExactBall(container, "14", "#ff9800", "#f57c00", 48, 12000, createPath1());
		createExactBall(container, "55", "#e91e63", "#c2185b", 45, 14000, createPath2());
		createExactBall(container, "28", "#ffc107", "#ff8f00", 50, 11000, createPath3());
		createExactBall(container, "71", "#8bc34a", "#689f38", 47, 13000, createPath4());
		createExactBall(container, "32", "#2196f3", "#1976d2", 46, 15000, createPath5());
		createExactBall(container, "01", "#9c27b0", "#7b1fa2", 49, 10000, createPath6());
		createExactBall(container, "90", "#ff5722", "#d84315", 44, 16000, createPath7());
	}

	private void createExactBall(Pane container, String number, String color1, String color2, int size, int duration,
			double[][] path) {
		Circle ball = new Circle(size / 2.0);

		RadialGradient ballGradient = new RadialGradient(0, 0, 0.3, 0.3, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, Color.web(color1)), new Stop(1, Color.web(color2)));

		ball.setFill(ballGradient);

		DropShadow ballShadow = new DropShadow();
		ballShadow.setRadius(12.5);
		ballShadow.setOffsetY(8);
		ballShadow.setColor(Color.color(0, 0, 0, 0.3));

		InnerShadow ballInnerLight = new InnerShadow();
		ballInnerLight.setRadius(6);
		ballInnerLight.setOffsetY(-4);
		ballInnerLight.setColor(Color.color(1, 1, 1, 0.3));

		InnerShadow ballInnerDark = new InnerShadow();
		ballInnerDark.setRadius(6);
		ballInnerDark.setOffsetY(4);
		ballInnerDark.setColor(Color.color(0, 0, 0, 0.2));

		ballInnerLight.setInput(ballInnerDark);
		ballShadow.setInput(ballInnerLight);
		ball.setEffect(ballShadow);

		Text text = new Text(number);
		text.setFill(Color.WHITE);
		text.setFont(customFont);

		DropShadow textShadow = new DropShadow();
		textShadow.setRadius(2);
		textShadow.setOffsetY(2);
		textShadow.setColor(Color.color(0, 0, 0, 0.7));
		text.setEffect(textShadow);

		StackPane ballContainer = new StackPane();
		ballContainer.getChildren().addAll(ball, text);
		ballContainer.setPrefSize(size, size);

		createExactAnimation(ballContainer, duration, path);

		container.getChildren().add(ballContainer);
	}

	private void createExactAnimation(StackPane ballContainer, int duration, double[][] path) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		for (int i = 0; i < path.length; i++) {
			double progress = (double) i / (path.length - 1);
			KeyFrame keyFrame = new KeyFrame(Duration.millis(duration * progress),
					new KeyValue(ballContainer.layoutXProperty(), path[i][0], Interpolator.EASE_BOTH),
					new KeyValue(ballContainer.layoutYProperty(), path[i][1], Interpolator.EASE_BOTH),
					new KeyValue(ballContainer.rotateProperty(), path[i][2], Interpolator.LINEAR));
			timeline.getKeyFrames().add(keyFrame);
		}

		timeline.play();
	}

	private double[][] createPath1() {
		return new double[][] { { 100, 130, 0 }, { 180, 90, 90 }, { 160, 180, 180 }, { 80, 160, 270 },
				{ 100, 130, 360 } };
	}

	private double[][] createPath2() {
		return new double[][] { { 170, 110, 0 }, { 140, 190, 72 }, { 200, 160, 144 }, { 170, 80, 216 },
				{ 110, 130, 288 }, { 170, 110, 360 } };
	}

	private double[][] createPath3() {
		return new double[][] { { 160, 200, 0 }, { 240, 140, 120 }, { 120, 100, 240 }, { 160, 200, 360 } };
	}

	private double[][] createPath4() {
		return new double[][] { { 90, 120, 0 }, { 180, 170, 108 }, { 210, 110, 216 }, { 90, 120, 360 } };
	}

	private double[][] createPath5() {
		return new double[][] { { 240, 170, 0 }, { 140, 240, 90 }, { 80, 110, 180 }, { 220, 90, 270 },
				{ 240, 170, 360 } };
	}

	private double[][] createPath6() {
		return new double[][] { { 160, 80, 0 }, { 110, 170, 144 }, { 200, 180, 288 }, { 160, 80, 360 } };
	}

	private double[][] createPath7() {
		return new double[][] { { 140, 240, 0 }, { 230, 160, 72 }, { 170, 70, 144 }, { 70, 120, 216 },
				{ 200, 220, 288 }, { 140, 240, 360 } };
	}

	private void createExactSparkles(Pane container) {
		createSparkle(container, 0.15 * DRUM_SIZE, 0.25 * DRUM_SIZE, 0);
		createSparkle(container, 0.70 * DRUM_SIZE, 0.20 * DRUM_SIZE, 1);
		createSparkle(container, 0.40 * DRUM_SIZE, 0.30 * DRUM_SIZE, 2);
		createSparkle(container, 0.30 * DRUM_SIZE, 0.85 * DRUM_SIZE, 3);
	}

	private void createSparkle(Pane container, double x, double y, int delay) {
		Circle sparkle = new Circle(2);
		sparkle.setFill(Color.color(1, 1, 1, 0.9));
		sparkle.setCenterX(x);
		sparkle.setCenterY(y);

		Timeline sparkleTimeline = new Timeline();
		sparkleTimeline.setCycleCount(Timeline.INDEFINITE);

		KeyFrame start = new KeyFrame(Duration.ZERO, new KeyValue(sparkle.opacityProperty(), 0),
				new KeyValue(sparkle.scaleXProperty(), 0), new KeyValue(sparkle.scaleYProperty(), 0));

		KeyFrame appear = new KeyFrame(Duration.millis(200), new KeyValue(sparkle.opacityProperty(), 1),
				new KeyValue(sparkle.scaleXProperty(), 1), new KeyValue(sparkle.scaleYProperty(), 1));

		KeyFrame stay = new KeyFrame(Duration.millis(3400), new KeyValue(sparkle.opacityProperty(), 1),
				new KeyValue(sparkle.scaleXProperty(), 1), new KeyValue(sparkle.scaleYProperty(), 1));

		KeyFrame fade = new KeyFrame(Duration.millis(4000), new KeyValue(sparkle.opacityProperty(), 0),
				new KeyValue(sparkle.scaleXProperty(), 0), new KeyValue(sparkle.scaleYProperty(), 0));

		sparkleTimeline.getKeyFrames().addAll(start, appear, stay, fade);
		sparkleTimeline.setDelay(Duration.seconds(delay));
		sparkleTimeline.play();

		container.getChildren().add(sparkle);
	}

	private ImageView createTitle() {
		Image logoImage = new Image("/Images/BONGI_Logo.png");
		ImageView title = new ImageView(logoImage);

		title.setFitHeight(400);
		title.setPreserveRatio(true);

		DropShadow titleShadow = new DropShadow();
		titleShadow.setRadius(15);
		titleShadow.setOffsetY(4);
		titleShadow.setColor(Color.color(0, 0, 0, 0.3));
		title.setEffect(titleShadow);

		ScaleTransition titleScale = new ScaleTransition(Duration.seconds(3), title);
		titleScale.setFromX(1.0);
		titleScale.setFromY(1.0);
		titleScale.setToX(1.02);
		titleScale.setToY(1.02);
		titleScale.setCycleCount(Timeline.INDEFINITE);
		titleScale.setAutoReverse(true);
		titleScale.setInterpolator(Interpolator.EASE_BOTH);
		titleScale.play();

		return title;
	}

}
