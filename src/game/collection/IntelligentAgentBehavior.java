package game.collection;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class IntelligentAgentBehavior extends JPanel implements ActionListener, MouseListener {

    private Point target; // target (lingkaran merah)
    private java.util.List<Agent> agents = new ArrayList<>();
    private Timer timer;

    public IntelligentAgentBehavior() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // posisi target awal di tengah
        target = new Point(400, 300);

        // buat agent sesuai tipe
        agents.add(new Agent(100, 100, Color.GRAY, "follow", 4, 0.5));
        agents.add(new Agent(700, 100, Color.BLACK, "follow", 2, 0.2));
        agents.add(new Agent(400, 500, Color.YELLOW, "run", 2, 0.3));

        addMouseListener(this);

        timer = new Timer(30, this); // update tiap 30ms
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // gambar target
        g.setColor(Color.RED);
        g.fillOval(target.x - 15, target.y - 15, 30, 30);

        // gambar agents
        for (Agent a : agents) {
            a.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Agent a : agents) {
            a.update(target);
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        target = e.getPoint(); // pindahkan target ke posisi klik
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // Kelas Agent
    static class Agent {

        double x, y;
        double speed;
        double turnRate;
        String mode;
        Color color;
        double vx = 0, vy = 0;

        public Agent(double x, double y, Color color, String mode, double speed, double turnRate) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.mode = mode;
            this.speed = speed;
            this.turnRate = turnRate;
        }

        public void update(Point target) {
            double dx = target.x - x;
            double dy = target.y - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < 1) {
                return;
            }

            // normalisasi arah
            double ndx = dx / distance;
            double ndy = dy / distance;

            // lerp (agar smooth pergerakannya)
            vx += turnRate * (ndx - vx);
            vy += turnRate * (ndy - vy);

            // normalisasi kecepatan
            double mag = Math.sqrt(vx * vx + vy * vy);
            vx = (vx / mag) * speed;
            vy = (vy / mag) * speed;

            if (mode.equals("follow")) {
                x += vx;
                y += vy;
            } else if (mode.equals("run")) {
                x -= vx;
                y -= vy;
            }
        }

        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);

            // gambar panah (segitiga) sebagai agent
            Polygon arrow = new Polygon();
            arrow.addPoint((int) x, (int) y);
            arrow.addPoint((int) (x - 10), (int) (y + 20));
            arrow.addPoint((int) (x + 10), (int) (y + 20));

            g2d.fill(arrow);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Intelligent Agent");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new IntelligentAgentBehavior());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
