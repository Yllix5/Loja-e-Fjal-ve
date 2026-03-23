import java.awt.Color;
import java.awt.Graphics;

public class Player {
    int x, y;
    static final int WIDTH = 50;
    static final int HEIGHT = 50;

    Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
       // g.setColor(Color.RED);
       // g.fillRect(x, y, WIDTH, HEIGHT);
    }
}
