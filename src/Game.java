import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class Game implements MouseListener, MouseMotionListener, ActionListener {
    //Hz for c4 through c5
    public final double[] NOTES = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};
    // Game States
    public static final double STATE_INFO = -1.0;
    public static final double STATE_MENU = 0.0;
    public static final double STATE_LEVEL1 = 1.1;
    public static final double STATE_LEVEL2 = 1.2;

    // Instance variables
    private GameView window;
    private double state;
    private int levelsCompleted;
    private Ball b;
    private Level currentLevel;

    // Constructor
    public Game() {
        // Ball
        b = new Ball (250.0, 50.0);
        // Front end
        window = new GameView(this);
        // Starting state is Menu
        state = -1;

        // Jpanel needs mouse listening capabilities
        this.window.getPanel().addMouseListener(this);
        this.window.getPanel().addMouseMotionListener(this);

        //Timer for tick and actionPerformed
        Timer tick = new Timer(16, this);
        tick.start();
        // Making level (temporary)
        currentLevel = new Level(Color.WHITE, new Rectangle(850, 850, 100, 100));
        currentLevel.addObstacle(new Obstacle(250, 700, 200, 25, Color.BLACK));
        currentLevel.addObstacle(new Obstacle(500, 550, 180, 25, Color.BLACK));
        currentLevel.addObstacle(new Obstacle(700, 400, 150, 25, Color.BLACK));
    }
    // Getters
    public double getState() {
        return state;
    }
    public Ball getBall() {
        return b;
    }
    public Level getCurrentLevel() {
        return currentLevel;
    }
    public boolean isInLevel() {
        return state >= 1;
    }


    // when mouse is pressed, check which state it is in and see if it hits buttons
    public void mousePressed(MouseEvent e) {
        // get mouse coordinates
        int x = e.getX();
        int y = e.getY();
        // If in info state and press close button, go to menu
        if (state == STATE_INFO) {
            // Check X button on instruction overlay
            if (x >= GameView.CLOSE_BTN_X && x <= GameView.CLOSE_BTN_X + GameView.CLOSE_BTN_SIZE
                    && y >= GameView.CLOSE_BTN_Y && y <= GameView.CLOSE_BTN_Y + GameView.CLOSE_BTN_SIZE) {
                state = STATE_MENU;
                window.repaint();
            }
            return;
        } else if (state == STATE_MENU) {
            // Check ? button (circle hit test)
            int dx = x - GameView.HELP_BTN_CX;
            int dy = y - GameView.HELP_BTN_CY;
            // see if mouse hits help button, change state and repaint if so
            if (Math.sqrt(dx * dx + dy * dy) <= GameView.HELP_BTN_RADIUS) {
                state = STATE_INFO;
                window.repaint();
                return;
            }

            // Level 1 button, change state to level 1 when user presses button
            if (x >= GameView.LEVEL1_X && x <= GameView.LEVEL1_X + GameView.LEVEL1_W
                    && y >= GameView.LEVEL1_Y && y <= GameView.LEVEL1_Y + GameView.LEVEL1_H) {
                state = STATE_LEVEL1;
                window.repaint();
                return;
            }
        }
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if (state >= 1.0) {
            b.tickStep(currentLevel);
        }
        window.repaint();
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    public static void main(String[] args) {
        Game g = new Game();
    }
}

