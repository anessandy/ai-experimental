package game.collection;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;


/**
 * EightPuzzleAStar.java
 *
 * 8-Puzzle solver with GUI (Java Swing) using A* (Manhattan distance). - Drag
 * or click tiles to move (click a tile adjacent to the blank). - Shuffle to
 * create a solvable random puzzle. - "Solve" uses A* to find the optimal
 * solution and animates the steps.
 *
 * Run: javac EightPuzzleAStar.java java EightPuzzleAStar
 */
public class EightPuzzle extends JFrame {

    private JButton[][] tiles = new JButton[3][3];
    private int[][] board = new int[3][3]; // 0 represents blank
    private JPanel boardPanel;
    private JButton shuffleBtn, solveBtn, resetBtn;
    private JLabel statusLabel;

    public EightPuzzle() {
        super("8-Puzzle (A* - Manhattan)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 480);
        setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(3, 3));
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 36);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton b = new JButton();
                b.setFont(f);
                b.setFocusable(false);
                final int rr = r, cc = c;
                b.addActionListener(e -> onTileClick(rr, cc));
                tiles[r][c] = b;
                boardPanel.add(b);
            }
        }

        JPanel control = new JPanel();
        shuffleBtn = new JButton("Shuffle");
        solveBtn = new JButton("Solve");
        resetBtn = new JButton("Reset");

        shuffleBtn.addActionListener(e -> shuffle());
        solveBtn.addActionListener(e -> solve());
        resetBtn.addActionListener(e -> reset());

        control.add(shuffleBtn);
        control.add(solveBtn);
        control.add(resetBtn);

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);

        add(boardPanel, BorderLayout.CENTER);
        add(control, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        reset();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void reset() {
        int v = 1;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = v % 9; // final state: 1..8, 0
                v++;
            }
        }
        updateUIFromBoard();
        statusLabel.setText("Solved state");
    }

    private void shuffle() {
        List<Integer> vals = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            vals.add(i);
        }
        do {
            Collections.shuffle(vals);
        } while (!isSolvable(vals));

        Iterator<Integer> it = vals.iterator();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = it.next();
            }
        }
        updateUIFromBoard();
        statusLabel.setText("Shuffled (solvable)");
    }

    // Check solvability for 8-puzzle: count inversions
    private boolean isSolvable(List<Integer> vals) {
        int inv = 0;
        for (int i = 0; i < vals.size(); i++) {
            for (int j = i + 1; j < vals.size(); j++) {
                int a = vals.get(i);
                int b = vals.get(j);
                if (a != 0 && b != 0 && a > b) {
                    inv++;
                }
            }
        }
        // for 3x3, solvable iff inversion count is even
        return inv % 2 == 0;
    }

    private void onTileClick(int r, int c) {
        // move tile if adjacent to blank
        int br = -1, bc = -1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    br = i;
                    bc = j;
                }
            }
        }
        if (Math.abs(br - r) + Math.abs(bc - c) == 1) {
            board[br][bc] = board[r][c];
            board[r][c] = 0;
            updateUIFromBoard();
            if (isGoal(board)) {
                statusLabel.setText("Solved!");
            } else {
                statusLabel.setText("Moved");
            }
        }
    }

    private void updateUIFromBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int val = board[r][c];
                JButton b = tiles[r][c];
                if (val == 0) {
                    b.setText("");
                    b.setBackground(Color.LIGHT_GRAY);
                    b.setEnabled(false);
                } else {
                    b.setText(String.valueOf(val));
                    b.setBackground(null);
                    b.setEnabled(true);
                }
            }
        }
    }

    private boolean isGoal(int[][] b) {
        int v = 1;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (b[r][c] != (v % 9)) {
                    return false;
                }
                v++;
            }
        }
        return true;
    }

    // Deep copy helper
    private int[][] copyBoard(int[][] b) {
        int[][] nb = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(b[i], 0, nb[i], 0, 3);
        }
        return nb;
    }

    // Convert board to a string key
    private String boardToKey(int[][] b) {
        StringBuilder sb = new StringBuilder(9);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                sb.append(b[r][c]);
            }
        }
        return sb.toString();
    }

    // Manhattan distance heuristic
    private int manhattan(int[][] b) {
        int dist = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int val = b[r][c];
                if (val == 0) {
                    continue;
                }
                int targetR = (val - 1) / 3;
                int targetC = (val - 1) % 3;
                dist += Math.abs(r - targetR) + Math.abs(c - targetC);
            }
        }
        return dist;
    }

    // A* implementation
    private List<int[][]> aStarSolve(int[][] start) {
        class Node implements Comparable<Node> {

            int[][] state;
            int g; // cost so far
            int f; // g + h
            Node parent;

            Node(int[][] s, int g, Node p) {
                this.state = s;
                this.g = g;
                this.parent = p;
                this.f = g + manhattan(s);
            }

            public int compareTo(Node o) {
                return Integer.compare(this.f, o.f);
            }
        }

        PriorityQueue<Node> open = new PriorityQueue<>();
        Set<String> closed = new HashSet<>();

        Node startNode = new Node(copyBoard(start), 0, null);
        open.add(startNode);

        while (!open.isEmpty()) {
            Node cur = open.poll();
            String key = boardToKey(cur.state);
            if (closed.contains(key)) {
                continue;
            }
            closed.add(key);

            if (isGoal(cur.state)) {
                // reconstruct path
                List<int[][]> path = new ArrayList<>();
                Node p = cur;
                while (p != null) {
                    path.add(p.state);
                    p = p.parent;
                }
                Collections.reverse(path);
                return path;
            }

            // find blank
            int br = -1, bc = -1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cur.state[i][j] == 0) {
                        br = i;
                        bc = j;
                    }
                }
            }

            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int nr = br + d[0], nc = bc + d[1];
                if (nr < 0 || nr >= 3 || nc < 0 || nc >= 3) {
                    continue;
                }
                int[][] ns = copyBoard(cur.state);
                // swap blank with neighbor
                ns[br][bc] = ns[nr][nc];
                ns[nr][nc] = 0;
                String nkey = boardToKey(ns);
                if (closed.contains(nkey)) {
                    continue;
                }
                Node child = new Node(ns, cur.g + 1, cur);
                open.add(child);
            }
        }

        return null; // unsolvable or not found
    }

    private void solve() {
        solveBtn.setEnabled(false);
        shuffleBtn.setEnabled(false);
        resetBtn.setEnabled(false);
        statusLabel.setText("Solving...");

        SwingWorker<List<int[][]>, Void> worker = new SwingWorkerImpl();
        worker.execute();
    }

    private void animatePath(List<int[][]> path) {
        if (path == null || path.size() <= 1) {
            statusLabel.setText("Already solved or no moves");
            return;
        }
        statusLabel.setText("Animating solution (" + (path.size() - 1) + " moves)");
        final int[] idx = {0};
        Timer t = new Timer(400, null);
        t.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idx[0]++;
                if (idx[0] >= path.size()) {
                    ((Timer) e.getSource()).stop();
                    statusLabel.setText("Solved (animated)");
                    return;
                }
                int[][] s = path.get(idx[0]);
                // copy s into board and update UI
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        board[r][c] = s[r][c];
                    }
                }
                updateUIFromBoard();
            }
        });
        // show first state then start
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c] = path.get(0)[r][c];
            }
        }
        updateUIFromBoard();
        t.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new EightPuzzle());
    }

    private class SwingWorkerImpl extends SwingWorker<List<int[][]>, Void> {

        public SwingWorkerImpl() {
        }

        protected List<int[][]> doInBackground() {
            return aStarSolve(board);
        }

        protected void done() {
            try {
                List<int[][]> path = get();
                if (path == null) {
                    JOptionPane.showMessageDialog(EightPuzzle.this, "No solution found (this should not happen for solvable puzzles)", "No Solution", JOptionPane.WARNING_MESSAGE);
                } else {
                    animatePath(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(EightPuzzle.this, "Error during solving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                solveBtn.setEnabled(true);
                shuffleBtn.setEnabled(true);
                resetBtn.setEnabled(true);
            }
        }
    }
}
