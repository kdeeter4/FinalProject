import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;;

public class Game implements MouseListener, MouseMotionListener {

    public final double[] NOTES = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};

    private GameView window;
    private double state;
    private int levelsCompleted;

    public Game() {
        window = new GameView(this);
        state = 0;

        this.window.addMouseListener(this);
        this.window.addMouseMotionListener(this);
    }


    public void mouseMoved(MouseEvent e) {
    }
    public void mouseDragged(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        Game g = new Game();
    }
}

