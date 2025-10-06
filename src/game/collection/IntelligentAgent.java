package game.collection;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class IntelligentAgent extends JPanel implements ActionListener, MouseListener {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int CELL_SIZE = 50;

    private Point agent = new Point(0, 0); // posisi agent
    private Point target = new Point(ROWS - 1, COLS - 1); // posisi target

    private java.util.List<Point> path = new ArrayList<>();
    private Timer timer;

    public IntelligentAgent() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setBackground(Color.WHITE);
        addMouseListener(this);

        timer = new Timer(300, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // gambar grid
        g.setColor(Color.LIGHT_GRAY);
        for (int r = 0; r <= ROWS; r++) {
            g.drawLine(0, r * CELL_SIZE, COLS * CELL_SIZE, r * CELL_SIZE);
        }
        for (int c = 0; c <= COLS; c++) {
            g.drawLine(c * CELL_SIZE, 0, c * CELL_SIZE, ROWS * CELL_SIZE);
        }

        // gambar agent
        g.setColor(Color.BLUE);
        g.fillRect(agent.x * CELL_SIZE + 5, agent.y * CELL_SIZE + 5,
                CELL_SIZE - 10, CELL_SIZE - 10);

        // gambar target
        g.setColor(Color.RED);
        g.fillRect(target.x * CELL_SIZE + 5, target.y * CELL_SIZE + 5,
                CELL_SIZE - 10, CELL_SIZE - 10);

        // gambar path
        g.setColor(Color.GREEN);
        for (Point p : path) {
            g.fillOval(p.x * CELL_SIZE + 20, p.y * CELL_SIZE + 20, 10, 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (path.isEmpty() || agent.equals(target)) {
            path = findPath(agent, target);
        } else {
            agent = path.remove(0);
        }
        repaint();
    }

    private java.util.List<Point> findPath(Point start, Point goal) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal));
        open.add(startNode);
        gScore.put(start, 0);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.point.equals(goal)) {
                return reconstructPath(cameFrom, current.point);
            }

            for (Point neighbor : getNeighbors(current.point)) {
                int tentative_g = gScore.get(current.point) + 1;
                if (tentative_g < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.point);
                    gScore.put(neighbor, tentative_g);
                    int f = tentative_g + heuristic(neighbor, goal);
                    open.add(new Node(neighbor, tentative_g, f));
                }
            }
        }
        return new ArrayList<>();
    }

    private java.util.List<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        LinkedList<Point> totalPath = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            totalPath.addFirst(current);
            current = cameFrom.get(current);
        }
        return totalPath;
    }

    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan distance
    }

    private java.util.List<Point> getNeighbors(Point p) {
        java.util.List<Point> neighbors = new ArrayList<>();
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nx = p.x + d[0];
            int ny = p.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < COLS && ny < ROWS) {
                neighbors.add(new Point(nx, ny));
            }
        }
        return neighbors;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;
        target = new Point(col, row);
        path = findPath(agent, target); // hitung ulang jalur
        repaint();
    }

    // Node class untuk A*
    private static class Node {
        Point point;
        int g, f;

        Node(Point p, int g, int f) {
            this.point = p;
            this.g = g;
            this.f = f;
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Intelligent Agent");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new IntelligentAgent());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

