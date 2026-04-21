import java.awt.*;
import javax.swing.*;

public class GameView extends JFrame {

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 1000;

    private Game backend;

    public GameView(Game backend) {

        this.backend = backend;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Tells the program what to do when the window is closed.
        this.setTitle("Game");                                // Sets the title of the window.
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);            // Sets the width and height of the window.
        this.setVisible(true);                                // Displays the window. SetVisible implicitly calls paint().
    }

    public void paint(Graphics g) {

    }

}