import java.awt.event.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class Game implements MouseListener, MouseMotionListener, ActionListener {

    public final double[] NOTES = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};

    private GameView window;
    private double state;
    private int levelsCompleted;
    private Ball b;

    public Game() {


        window = new GameView(this);
        state = -1;

        b = new Ball (50.0, 50.0);

        this.window.addMouseListener(this);
        this.window.addMouseMotionListener(this);

        Timer tick = new Timer(50, this);
        tick.start();
    }

    public double getState() {
        return state;
    }

    public void mouseMoved(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        // Adjust y for title bar inset so logical coords match what was drawn
        int y = e.getY() - window.getInsets().top;

        if (state == -1) {
            // Check X button on instruction overlay
            if (x >= GameView.CLOSE_BTN_X && x <= GameView.CLOSE_BTN_X + GameView.CLOSE_BTN_SIZE
                    && y >= GameView.CLOSE_BTN_Y && y <= GameView.CLOSE_BTN_Y + GameView.CLOSE_BTN_SIZE) {
                state = 0;
                window.repaint();
            }
        } else if (state == 0) {
            // Check ? button (circle hit test)
            int dx = x - GameView.HELP_BTN_CX;
            int dy = y - GameView.HELP_BTN_CY;
            // Checks if in circle
            if (Math.sqrt(dx * dx + dy * dy) <= GameView.HELP_BTN_RADIUS) {
                state = -1;
                window.repaint();
            }
            // Level 1 box — no action yet
        }
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        Game g = new Game();
    }

    public Ball getBall() {
        return b;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        b.tickStep();
        window.repaint();
    }
}

