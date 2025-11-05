import java.awt.*;
import java.util.Random;

public class Ball extends Rectangle {
    int xVelocity;
    int yVelocity;
    int initialSpeed = 5;

    public Ball(int x, int y, int width, int height) {
        super(x, y, width, height);
        Random random = new Random();
        int randomXDirection = random.nextBoolean() ? 1 : -1;
        int randomYDirection = random.nextBoolean() ? 1 : -1;

        xVelocity = initialSpeed * randomXDirection;
        yVelocity = initialSpeed * randomYDirection;
    }

    public void setXDirection(int xDirection) {
        xVelocity = xDirection;
    }

    public void setYDirection(int yDirection) {
        yVelocity = yDirection;
    }

    public void move() {
        x += xVelocity;
        y += yVelocity;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
}
