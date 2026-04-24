import java.awt.event.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class Game implements MouseListener, MouseMotionListener, ActionListener {

    public final double[] NOTES = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};

    public static final double STATE_INFO = -1.0;
    public static final double STATE_MENU = 0.0;
    public static final double STATE_LEVEL1 = 1.1;
    public static final double STATE_LEVEL2 = 1.2;
    public static final double STATE_WORLD2_LEVEL1 = 2.1;


    private GameView window;
    private double state;
    private int levelsCompleted;
    private Ball b;


    public Game() {
        b = new Ball (50.0, 50.0);
        window = new GameView(this);
        state = -1;

        this.window.getPanel().addMouseListener(this);
        this.window.getPanel().addMouseMotionListener(this);

        Timer tick = new Timer(16, this);
        tick.start();
    }

    public double getState() {
        return state;
    }

    public Ball getBall() {
        return b;
    }

    public boolean isInLevel() {
        return state >= 1;
    }



    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        // Adjust y for title bar inset so logical coords match what was drawn
        int y = e.getY();

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

            if (Math.sqrt(dx * dx + dy * dy) <= GameView.HELP_BTN_RADIUS) {
                state = STATE_INFO;
                window.repaint();
                return;
            }

            // Level 1 button
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
            b.tickStep();
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

