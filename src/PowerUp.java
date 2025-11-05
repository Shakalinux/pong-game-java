import java.awt.*;

public class PowerUp {
    int x, y;
    int diameter = 20;
    PowerUpType type;

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = getRandomPowerUpType();
    }

    private PowerUpType getRandomPowerUpType() {
        PowerUpType[] values = PowerUpType.values();
        int index = (int) (Math.random() * values.length);
        return values[index];
    }

    public void draw(Graphics g) {
        switch (type) {
            case INCREASE_PADDLE -> g.setColor(Color.GREEN);
            case DECREASE_PADDLE_OPPONENT -> g.setColor(Color.RED);
            case INCREASE_BALL_SPEED -> g.setColor(Color.ORANGE);
            case DECREASE_BALL_SPEED -> g.setColor(Color.CYAN);
            case FREEZE_OPPONENT -> g.setColor(Color.BLUE);
            case REVERSE_CONTROLS -> g.setColor(Color.MAGENTA);
        }
        g.fillOval(x, y, diameter, diameter);
    }

    public boolean checkCollision(Ball ball) {
        Rectangle ballRect = new Rectangle(ball.x, ball.y, ball.width, ball.height);
        Rectangle powerRect = new Rectangle(x, y, diameter, diameter);
        return ballRect.intersects(powerRect);
    }
}
