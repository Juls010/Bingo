package main;
import model.Drum;

public class Main {
    public static void main(String[] args) {
        Drum drum = new Drum();
        

        for (int i = 0; i < 95; i++) { // Intentamos sacar más de 90 bolas
            Integer ball = drum.drawBall();
            if (ball == null) {
                System.out.println("All balls have been drawn. The game is over!");
                break;
            } else {
                System.out.println("New ball: " + ball);
            }
        }
    }
}

