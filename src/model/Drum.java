package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Drum {

	private static final int MAX_BALL_NUMBER = 90;

	private List<Integer> availableBalls;
	private List<Integer> drawnBalls;

	public Drum() {
		reset();
	}

	public void reset() {
		availableBalls = new ArrayList<>();
		for (int i = 1; i <= MAX_BALL_NUMBER; i++) {
			availableBalls.add(i);
		}
		Collections.shuffle(availableBalls);
		drawnBalls = new ArrayList<>();

	}

	public List<Integer> getLastBalls(int count) {
		int size = drawnBalls.size();
		if (size <= count) {
			return new ArrayList<>(drawnBalls);
		}
		return drawnBalls.subList(size - count, size);
	}

	public Integer drawBall() {
		if (availableBalls.isEmpty()) {
			return null;
		}

		int ball = availableBalls.remove(0);
		drawnBalls.add(ball);
		return ball;
	}

	public List<Integer> getDrawnBalls() {
		return new ArrayList<>(drawnBalls);
	}

	public boolean hasBallsLeft() {
		return !availableBalls.isEmpty();
	}

	public static int getMaxBallNumber() {
		return MAX_BALL_NUMBER;
	}
}
