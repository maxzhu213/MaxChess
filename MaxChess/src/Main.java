import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Max on 2018-04-25.
 */
public class Main extends JPanel implements ActionListener {

    private final String TITLE = "Max Chess";
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 690;

    private JFrame frame;
    private Timer timer;

    private Board board;

    private Main() {
        setUpFrame();
        board = new Board();
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                board.onClick(e.getX(), e.getY());
            }
        });
    }

    private void setUpFrame() {
        frame = new JFrame(TITLE);
        timer = new Timer(25, this);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setOpaque(false);
        frame.setResizable(false);
        frame.add(this);
        timer.start();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }


    @Override
    public void paintComponent(Graphics g) {
        if (board != null) {
            board.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
