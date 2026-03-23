import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

public class Word {
    private String word;
    private int x, y, speed;
    private static final int WIDTH = 200; // Adjusted for word width
    private static final int HEIGHT = 30;

    public Word(String word, int x, int speed) {
        this.word = word;
        this.x = x;
        this.y = -HEIGHT; // Start above the screen
        this.speed = speed;
    }

    public String getWord() {
        return word;
    }

    public int getY() {
        return y;
    }

    public void move() {
        y += speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.PLAIN, 24));
        g.drawString(word, x, y + HEIGHT / 2);
    }
}
