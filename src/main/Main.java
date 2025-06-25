package main;
import model.Drum;

public class Main {
    public static void main(String[] args) {
        Drum drum = new Drum();
        
        while(drum.hasBallsLeft()) {
        	Integer ball = drum.drawBall();
        	System.out.println("New ball: " + ball);
        }
        System.out.println("Alls balls have been drawn. The game is over!");
          
    }
}

