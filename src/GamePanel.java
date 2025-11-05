import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * 0.5555);
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    PowerUp powerUp;
    long powerUpSpawnTime = 0;
    final long powerUpDuration = 5000;
    boolean powerUpActive = false;
    long powerUpActivatedTime = 0;

    boolean paddle2Frozen = false;
    long freezeStartTime = 0;

    boolean controlsReversed = false;
    long reverseStartTime = 0;

    Thread gameThread;
    Image image;
    Graphics graphics;

    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Image backgroundImage;

    int player1Score = 0;
    int player2Score = 0;

    boolean singlePlayer;

    SoundPlayer soundPlayer = new SoundPlayer();

    public GamePanel(boolean singlePlayer) {
        this.singlePlayer = singlePlayer;

        newPaddles();
        newBall();

        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        try {
            backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("/background.jpeg"))).getImage();
        } catch (Exception e) {
            System.err.println("Erro ao carregar background.jpeg: " + e.getMessage());
            backgroundImage = null;
        }

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newBall() {
        ball = new Ball(GAME_WIDTH / 2 - BALL_DIAMETER / 2,
            GAME_HEIGHT / 2 - BALL_DIAMETER / 2,
            BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2),
            PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2),
            PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) {
            image = createImage(getWidth(), getHeight());
            if (image != null) {
                graphics = image.getGraphics();
            }
        }

        if (graphics != null) {
            draw(graphics);
            g.drawImage(image, 0, 0, this);
        }
    }

    public void draw(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }

        if (powerUp != null) {
            powerUp.draw(g);
        }

        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 40));
        g.drawString(String.valueOf(player1Score), GAME_WIDTH / 2 - 50, 50);
        g.drawString(String.valueOf(player2Score), GAME_WIDTH / 2 + 25, 50);
    }

    public void move() {
        paddle1.move();

        if (singlePlayer) {
            if (!paddle2Frozen) {
                movePaddle2AI();
            }
        } else {
            if (!paddle2Frozen) {
                paddle2.move();
            }
        }

        ball.move();
    }

    private void movePaddle2AI() {
        int paddleCenter = paddle2.y + paddle2.height / 2;

        int totalScore = player1Score + player2Score;
        int aiSpeed = Math.min(15, 7 + totalScore / 2);

        if (paddleCenter < ball.y) {
            paddle2.y += aiSpeed;
        } else if (paddleCenter > ball.y + BALL_DIAMETER) {
            paddle2.y -= aiSpeed;
        }

        if (paddle2.y < 0) paddle2.y = 0;
        if (paddle2.y > GAME_HEIGHT - paddle2.height) paddle2.y = GAME_HEIGHT - paddle2.height;
    }

    public void checkCollision() {
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
            soundPlayer.playSound("hit.wav");
        }

        if (ball.intersects(paddle1)) {
            ball.xVelocity = Math.abs(ball.xVelocity);

            if (ball.xVelocity < 15) ball.xVelocity += 1;
            if (ball.yVelocity > 0 && ball.yVelocity < 15) ball.yVelocity += 1;
            else if (ball.yVelocity < 0 && ball.yVelocity > -15) ball.yVelocity -= 1;

            ball.setXDirection(ball.xVelocity);
            soundPlayer.playSound("hit.wav");
        }

        if (ball.intersects(paddle2)) {
            ball.xVelocity = -Math.abs(ball.xVelocity);

            if (ball.xVelocity > -15) ball.xVelocity -= 1;
            if (ball.yVelocity > 0 && ball.yVelocity < 15) ball.yVelocity += 1;
            else if (ball.yVelocity < 0 && ball.yVelocity > -15) ball.yVelocity -= 1;

            ball.setXDirection(ball.xVelocity);
            soundPlayer.playSound("hit.wav");
        }

        paddle1.y = Math.max(0, Math.min(paddle1.y, GAME_HEIGHT - paddle1.height));
        paddle2.y = Math.max(0, Math.min(paddle2.y, GAME_HEIGHT - paddle2.height));

        if (ball.x <= 0) {
            player2Score++;
            soundPlayer.playSound("score.wav");
            resetAfterScore();
        }

        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            player1Score++;
            soundPlayer.playSound("score.wav");
            resetAfterScore();
        }
    }

    private void resetAfterScore() {
        newBall();
        newPaddles();
        powerUp = null;
        powerUpActive = false;
        paddle1.height = PADDLE_HEIGHT;
        paddle2.height = PADDLE_HEIGHT;
        paddle2Frozen = false;
        controlsReversed = false;
    }

    private void applyPowerUp(PowerUpType type) {
        switch (type) {
            case INCREASE_PADDLE:
                paddle1.height = Math.min(GAME_HEIGHT / 2, paddle1.height + 50);
                break;
            case DECREASE_PADDLE_OPPONENT:
                paddle2.height = Math.max(30, paddle2.height - 50);
                break;
            case INCREASE_BALL_SPEED:
                ball.xVelocity *= 1.5;
                ball.yVelocity *= 1.5;
                break;
            case DECREASE_BALL_SPEED:
                ball.xVelocity *= 0.7;
                ball.yVelocity *= 0.7;
                break;
            case FREEZE_OPPONENT:
                paddle2Frozen = true;
                freezeStartTime = System.currentTimeMillis();
                break;
            case REVERSE_CONTROLS:
                controlsReversed = true;
                reverseStartTime = System.currentTimeMillis();
                break;
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1_000_000_000 / amountOfTicks;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                move();
                checkCollision();

                long nowMillis = System.currentTimeMillis();


                if (powerUp == null && nowMillis - powerUpSpawnTime > 15000 + (int) (Math.random() * 15000)) {
                    int x = GAME_WIDTH / 2 - 10;
                    int y = 50 + (int) (Math.random() * (GAME_HEIGHT - 100));
                    powerUp = new PowerUp(x, y);
                    powerUpSpawnTime = nowMillis;
                }

                if (powerUp != null && powerUp.checkCollision(ball)) {
                    applyPowerUp(powerUp.type);
                    powerUpActive = true;
                    powerUpActivatedTime = System.currentTimeMillis();
                    powerUp = null;
                }


                if (powerUpActive) {
                    if (System.currentTimeMillis() - powerUpActivatedTime > powerUpDuration) {
                        powerUpActive = false;
                        paddle1.height = PADDLE_HEIGHT;
                        paddle2.height = PADDLE_HEIGHT;
                        paddle2Frozen = false;
                        controlsReversed = false;
                    }
                }

                if (paddle2Frozen && System.currentTimeMillis() - freezeStartTime > powerUpDuration) {
                    paddle2Frozen = false;
                }


                if (controlsReversed && System.currentTimeMillis() - reverseStartTime > powerUpDuration) {
                    controlsReversed = false;
                }

                repaint();
                delta--;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (controlsReversed) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> paddle1.keyPressed(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), KeyEvent.VK_S, e.getKeyChar()));
                    case KeyEvent.VK_S -> paddle1.keyPressed(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), KeyEvent.VK_W, e.getKeyChar()));
                    default -> paddle1.keyPressed(e);
                }
            } else {
                paddle1.keyPressed(e);
            }

            if (!singlePlayer) {
                paddle2.keyPressed(e);
            }
        }

        public void keyReleased(KeyEvent e) {
            if (controlsReversed) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> paddle1.keyReleased(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), KeyEvent.VK_S, e.getKeyChar()));
                    case KeyEvent.VK_S -> paddle1.keyReleased(new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), KeyEvent.VK_W, e.getKeyChar()));
                    default -> paddle1.keyReleased(e);
                }
            } else {
                paddle1.keyReleased(e);
            }

            if (!singlePlayer) {
                paddle2.keyReleased(e);
            }
        }
    }
}
