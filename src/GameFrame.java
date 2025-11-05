import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame(boolean singlePlayer) {
        this.add(new GamePanel(singlePlayer));
        this.setTitle("Ping Pong");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
