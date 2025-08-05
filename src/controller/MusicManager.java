package controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {

	public enum MusicType {
		NONE, WELCOME, GAME
	}

	private boolean isMuted = false;
	private MediaPlayer currentPlayer;
	private Media welcomeMusic;
	private Media gameMusic;
	private MusicType currentMusic = MusicType.NONE;

	public MusicManager() {
		try {
			welcomeMusic = new Media(
					getClass().getResource("/music/Mariachi Madness - Jimena Contreras.mp3").toExternalForm());
			gameMusic = new Media(
					getClass().getResource("/music/George Street Shuffle - Kevin MacLeod.mp3").toExternalForm());
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void playWelcomeMusic() {
		if (currentMusic == MusicType.WELCOME && currentPlayer != null) {
			currentPlayer.play();
			return;
		}
		stopMusic();
		currentMusic = MusicType.WELCOME;
		currentPlayer = new MediaPlayer(welcomeMusic);
		currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		if (!isMuted) {
			currentPlayer.play();
		}
	}

	public void playGameMusic() {
		if (currentMusic == MusicType.GAME && currentPlayer != null) {
			currentPlayer.play();
			return;
		}
		stopMusic();
		currentMusic = MusicType.GAME;
		currentPlayer = new MediaPlayer(gameMusic);
		currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		if (!isMuted) {
			currentPlayer.play();
		}
	}

	public void toggleMute() {
		if (currentPlayer == null) return;

		isMuted = !isMuted;

		if (isMuted) {
			currentPlayer.pause(); 
		} else {
			currentPlayer.play(); 
		}
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void stopMusic() {
		if (currentPlayer != null) {
			currentPlayer.stop();
			currentPlayer.dispose();
			currentPlayer = null;
		}
	}

}
