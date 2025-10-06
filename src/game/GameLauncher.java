package game;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameLauncher extends JFrame {
    
    // Daftar nama kelas game yang ingin Anda luncurkan
    private final String[] gameList = {
        "EightPuzzle",
        "House3D",
        "IntelligentAgent",
        "TicTacToeUnbeatable",
        "IntelligentAgentBehaviour"
    };
    
    public GameLauncher() {
        setTitle("Java Game Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(gameList.length, 1, 10, 10)); // Tata letak grid

        createGameButtons();

        pack(); // Mengatur ukuran jendela agar pas dengan komponen
        setLocationRelativeTo(null); // Menempatkan jendela di tengah layar
        setVisible(true);
    }
    
    private void createGameButtons() {
        for (String gameName : gameList) {
            JButton button = new JButton("Run " + gameName + " Game");
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            // Tambahkan ActionListener ke tombol
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Panggil method untuk menjalankan game
                    runGame(gameName);
                }
            });
            add(button);
        }
    }
    
    /**
     * Metode untuk menjalankan kelas game tertentu menggunakan Reflection.
     * Ini mencoba memanggil main(String[] args) dari kelas game.
     */
    private void runGame(String className) {
        System.out.println("Launching: " + className + "...");
        try {
            // Dapatkan kelas berdasarkan nama
            Class<?> gameClass = Class.forName("game.collection."+className);

            // Cari method main(String[] args)
            Method mainMethod = gameClass.getMethod("main", String[].class);

            // Panggil method main
            // Parameter pertama adalah instance (null karena main adalah static),
            // Parameter kedua adalah argumen (array String kosong)
            mainMethod.invoke(null, (Object) new String[0]);

            // Tutup jendela launcher setelah meluncurkan game (opsional)
            // dispose(); 

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Game class not found: " + className, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            JOptionPane.showMessageDialog(this, "The class " + className + " does not have a main(String[] args) method.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred while running the game: " + className + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT) untuk keamanan thread Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameLauncher();
            }
        });
    }
}
