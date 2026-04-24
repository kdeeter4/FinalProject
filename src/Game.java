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
        state = -1;

        this.window.addMouseListener(this);
        this.window.addMouseMotionListener(this);
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

        // --- Tune scoring test ---
        int q = 400;  // quarter note
        int h = 800;  // half note
        int gap = 50;

        // Original tune: C4 G4 A4 G4
        Tune original = new Tune(new Tune.NoteEvent[] {
                new Tune.NoteEvent(new Note("C4"), q, gap),
                new Tune.NoteEvent(new Note("G4"), q, gap),
                new Tune.NoteEvent(new Note("A4"), q, gap),
                new Tune.NoteEvent(new Note("G4"), h, gap),
        });

        // Slightly off: one wrong note (E4 instead of G4), one slightly different duration
        Tune attempt = new Tune(new Tune.NoteEvent[] {
                new Tune.NoteEvent(new Note("C4"), h, 99),      // same
                new Tune.NoteEvent(new Note("E4"), gap, 35),      // wrong note (was G4)
                new Tune.NoteEvent(new Note("A4"), 350, 2),    // right note, slightly short
                new Tune.NoteEvent(new Note("G5"), h, 5),      // same
        });

        // Exact copy — should be 100
        Tune exact = new Tune(new Tune.NoteEvent[] {
                new Tune.NoteEvent(new Note("C4"), q, gap),
                new Tune.NoteEvent(new Note("G4"), q, gap),
                new Tune.NoteEvent(new Note("A4"), q, gap),
                new Tune.NoteEvent(new Note("G4"), h, gap),
        });

        System.out.println("Score (slightly off): " + original.score(attempt));  // expect ~70-85
        System.out.println("Score (exact match):  " + original.score(exact));    // expect 100
    }
}