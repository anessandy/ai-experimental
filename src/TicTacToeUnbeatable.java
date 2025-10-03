
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * TicTacToeUnbeatable.java
 *
 * Game Tic-Tac-Toe sederhana menggunakan Java Swing. - Pemain manusia bermain
 * sebagai 'X', komputer sebagai 'O'. - Komputer menggunakan algoritma Minimax
 * sehingga tidak bisa dikalahkan (akan menang atau seri jika dimainkan
 * sempurna).
 *
 * Cara jalankan: javac TicTacToeUnbeatable.java java TicTacToeUnbeatable
 */
public class TicTacToeUnbeatable extends JFrame implements ActionListener {

    private JButton[][] buttons = new JButton[3][3];
    private char[][] board = new char[3][3]; // 'X', 'O', or '\u0000' (empty)
    private boolean humanTurn = true; // human starts
    private JLabel statusLabel;
    private JButton resetButton;

    public TicTacToeUnbeatable() {
        super("Tic-Tac-Toe (Unbeatable AI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 420);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        boardPanel.setPreferredSize(new Dimension(360, 360));

        Font btnFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c] = new JButton("");
                buttons[r][c].setFont(btnFont);
                buttons[r][c].setFocusPainted(false);
                buttons[r][c].addActionListener(this);
                boardPanel.add(buttons[r][c]);
                board[r][c] = '\u0000';
            }
        }

        JPanel bottom = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Giliran Anda (X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        bottom.add(statusLabel, BorderLayout.CENTER);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        bottom.add(resetButton, BorderLayout.EAST);

        add(boardPanel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!humanTurn) {
            return; // ignore clicks while AI is thinking
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (e.getSource() == buttons[r][c]) {
                    if (board[r][c] == '\u0000') {
                        makeMove(r, c, 'X');
                        if (isTerminal()) {
                            return;
                        }
                        humanTurn = false;
                        statusLabel.setText("Komputer berpikir...");

                        // small delay so user can see their move before AI moves
                        Timer t = new Timer(250, ev -> {
                            aiMove();
                            humanTurn = true;
                        });
                        t.setRepeats(false);
                        t.start();
                    }
                    return;
                }
            }
        }
    }

    private void makeMove(int r, int c, char player) {
        board[r][c] = player;
        buttons[r][c].setText(String.valueOf(player));
        buttons[r][c].setEnabled(false);

        char winner = checkWinner();
        if (winner == 'X') {
            statusLabel.setText("Anda menang!");
            endGame("Anda menang!");
        } else if (winner == 'O') {
            statusLabel.setText("Komputer menang");
            endGame("Komputer menang");
        } else if (isBoardFull()) {
            statusLabel.setText("Seri");
            endGame("Seri");
        } else {
            if (player == 'X') {
                statusLabel.setText("Giliran Komputer (O)");
            } else {
                statusLabel.setText("Giliran Anda (X)");
            }
        }
    }

    private void aiMove() {
        int[] best = findBestMove();
        if (best != null) {
            makeMove(best[0], best[1], 'O');
        }
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = null;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == '\u0000') {
                    board[r][c] = 'O';
                    int score = minimax(board, 0, false);
                    board[r][c] = '\u0000';
                    if (score > bestScore) {
                        bestScore = score;
                        move = new int[]{r, c};
                    }
                }
            }
        }
        return move;
    }

    // Minimax: isMaximizing == true -> AI's turn to move (O)
    private int minimax(char[][] b, int depth, boolean isMaximizing) {
        char winner = checkWinner(b);
        if (winner == 'O') {
            return 10 - depth; // prefer faster win
        }
        if (winner == 'X') {
            return depth - 10; // prefer slower loss
        }
        if (isFull(b)) {
            return 0;
        }

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (b[r][c] == '\u0000') {
                        b[r][c] = 'O';
                        int val = minimax(b, depth + 1, false);
                        b[r][c] = '\u0000';
                        best = Math.max(best, val);
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (b[r][c] == '\u0000') {
                        b[r][c] = 'X';
                        int val = minimax(b, depth + 1, true);
                        b[r][c] = '\u0000';
                        best = Math.min(best, val);
                    }
                }
            }
            return best;
        }
    }

    private boolean isTerminal() {
        char winner = checkWinner();
        if (winner != '\u0000' || isBoardFull()) {
            // game already ended
            return true;
        }
        return false;
    }

    private void endGame(String message) {
        // disable all buttons
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c].setEnabled(false);
            }
        }
        statusLabel.setText(message + " (Tekan Reset untuk main lagi)");
    }

    private void resetGame() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = '\u0000';
                buttons[r][c].setText("");
                buttons[r][c].setEnabled(true);
            }
        }
        humanTurn = true;
        statusLabel.setText("Giliran Anda (X)");
    }

    private boolean isBoardFull() {
        return isFull(board);
    }

    private boolean isFull(char[][] b) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (b[r][c] == '\u0000') {
                    return false;
                }
            }
        }
        return true;
    }

    // Check winner on current board
    private char checkWinner() {
        return checkWinner(this.board);
    }

    // Check winner on arbitrary board
    private char checkWinner(char[][] b) {
        // rows & cols
        for (int i = 0; i < 3; i++) {
            if (b[i][0] != '\u0000' && b[i][0] == b[i][1] && b[i][1] == b[i][2]) {
                return b[i][0];
            }
            if (b[0][i] != '\u0000' && b[0][i] == b[1][i] && b[1][i] == b[2][i]) {
                return b[0][i];
            }
        }
        // diagonals
        if (b[0][0] != '\u0000' && b[0][0] == b[1][1] && b[1][1] == b[2][2]) {
            return b[0][0];
        }
        if (b[0][2] != '\u0000' && b[0][2] == b[1][1] && b[1][1] == b[2][0]) {
            return b[0][2];
        }
        return '\u0000';
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new TicTacToeUnbeatable());
    }
}
