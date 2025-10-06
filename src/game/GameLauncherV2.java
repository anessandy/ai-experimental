package game;

import game.collection.EightPuzzle;
import game.collection.House3D;
import game.collection.IntelligentAgent;
import game.collection.IntelligentAgentBehavior;
import game.collection.TicTacToeUnbeatable;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameLauncherV2 extends JFrame {

    public GameLauncherV2() {
        setTitle("Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton btnEightPuzzle = new JButton("Eight Puzzle");
        JButton btnHouse3D = new JButton("House 3D");
        JButton btnIntelligentAgent = new JButton("Intelligent Agent");
        JButton btnIntelligentAgentBehavior = new JButton("Intelligent Agent Behavior");
        JButton btnTicTacToe = new JButton("Tic Tac Toe Unbeatable");
        JButton btnExit = new JButton("Keluar");

        panel.add(btnEightPuzzle);
        panel.add(btnHouse3D);
        panel.add(btnIntelligentAgent);
        panel.add(btnIntelligentAgentBehavior);
        panel.add(btnTicTacToe);
        panel.add(btnExit);

        add(panel);

        btnEightPuzzle.addActionListener(e -> EightPuzzle.main(null));
        btnHouse3D.addActionListener(e -> {
            new Thread(() -> {
                new House3D().run();
            }).start();
        });
        btnIntelligentAgent.addActionListener(e -> IntelligentAgent.main(null));
        btnIntelligentAgentBehavior.addActionListener(e -> IntelligentAgentBehavior.main(null));
        btnTicTacToe.addActionListener(e -> TicTacToeUnbeatable.main(null));
        btnExit.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameLauncherV2().setVisible(true);
        });
    }
}

