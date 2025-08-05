package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.io.InputStream;

public class DecorativeImage {

	private final StackPane container;
	private ImageView leftImage;
	private ImageView rightImage;

	public DecorativeImage(StackPane container) {
		this.container = container;
	}

	public void loadDecorativeImages(String leftImagePath, String rightImagePath) {
		leftImage = createResponsiveImage(leftImagePath, true);
		rightImage = createResponsiveImage(rightImagePath, false);

		addImagesToContainer();
	}

	private ImageView createResponsiveImage(String resourcePath, boolean isLeft) {
		try {
			InputStream imageStream = getClass().getResourceAsStream(resourcePath);
			if (imageStream == null) {
				imageStream = getClass().getResourceAsStream("/resources/Images" + resourcePath);
				if (imageStream == null) {
					return null;
				}
			}

			Image image = new Image(imageStream);

			ImageView imageView = new ImageView(image);

			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			imageView.setCache(true);
			imageView.setMouseTransparent(true);

			imageView.fitHeightProperty().bind(container.heightProperty());

			StackPane.setMargin(imageView, new Insets(0));
			return imageView;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void addImagesToContainer() {
		if (leftImage != null) {
			StackPane.setAlignment(leftImage, Pos.CENTER_LEFT);
			container.getChildren().add(leftImage);
		}

		if (rightImage != null) {
			StackPane.setAlignment(rightImage, Pos.CENTER_RIGHT);
			container.getChildren().add(rightImage);
		}
	}

	public void setVisible(boolean visible) {
		if (leftImage != null) {
			leftImage.setVisible(visible);
		}
		if (rightImage != null) {
			rightImage.setVisible(visible);
		}
	}

	public void showForWelcomeScreen() {
		setVisible(true);
	}

	public void hideForGameScreen() {
		setVisible(false);
	}

	public ImageView getLeftImage() {
		return leftImage;
	}

	public ImageView getRightImage() {
		return rightImage;
	}

	public void removeImages() {
		if (leftImage != null) {
			container.getChildren().remove(leftImage);
		}
		if (rightImage != null) {
			container.getChildren().remove(rightImage);
		}
	}

	public void setMaxImageSize(double maxHeight, double maxWidth) {
		if (leftImage != null) {
			leftImage.setFitHeight(maxHeight);
			if (maxWidth > 0) {
				leftImage.setFitWidth(maxWidth);
			}
		}
		if (rightImage != null) {
			rightImage.setFitHeight(maxHeight);
			if (maxWidth > 0) {
				rightImage.setFitWidth(maxWidth);
			}
		}
	}
}
