import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMenu extends JFrame {

    public GameMenu() {
        this.setTitle("Escolha o modo de jogo");
        this.setSize(400, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(4, 2));

        JLabel label = new JLabel("Selecione o modo de jogo", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        this.add(label);

        JButton singlePlayerBtn = new JButton("1 Jogador");
        JButton twoPlayerBtn = new JButton("2 Jogadores");

        this.add(singlePlayerBtn);
        this.add(twoPlayerBtn);

        singlePlayerBtn.addActionListener(e -> {
            startGame(true);
        });

        twoPlayerBtn.addActionListener(e -> {
            startGame(false);
        });

        this.setVisible(true);
    }

    private void startGame(boolean singlePlayer) {
        this.dispose();
        new GameFrame(singlePlayer);
    }

    public static void main(String[] args) {
        new GameMenu();
    }
}
