import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 2018-04-25.
 */
public class Main extends JPanel implements ActionListener {

    final String TITLE = "Max Chess";
    final int SCREEN_WIDTH = 800;
    final int SCREEN_HEIGHT = 690;


    JFrame frame;
    Timer timer;

    Board board;


    Main() {
        setUpFrame();
        board = new Board();
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                board.onClick(e.getX(), e.getY());
            }
        });
    }


    public void setUpFrame() {
        frame = new JFrame(TITLE);
        timer = new Timer(25, this);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        this.setOpaque(false);
        frame.add(this);
        timer.start();
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
