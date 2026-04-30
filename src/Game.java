import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;


public class Game implements MouseListener, MouseMotionListener, ActionListener, NoteBlockListener {

    // Game States
    public static final double STATE_INFO = -1.0;
    public static final double STATE_MENU = 0.0;
    public static final double STATE_LEVEL1 = 1.01;
    public static final double STATE_LEVEL2 = 1.02;
    public static final double STATE_WIN = 2;
    public static final double STATE_SCORE_SCREEN = 3;

    // Instance variables
    private GameView window;
    private double state;
    private int levelsCompleted;
    private Ball b;
    private Level currentLevel;
    private NoteBlock dragging = null;   // block being dragged
    private int dragOffsetX, dragOffsetY;
    private List<NoteBlock> palette;     // available blocks on the side
    private TuneRecorder tuneRecorder;

    // Constructor
    public Game() {
        // Ball
        b = new Ball (250.0, 50.0);
        // Front end
        window = new GameView(this);
        // Starting state is Menu
        state = -1;

        tuneRecorder = new TuneRecorder();

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
        palette = new ArrayList<>();
        String[] noteNames = {"C4","D4","E4","F4","G4","A4","B4"};
        for (int i = 0; i < noteNames.length; i++) {
            palette.add(new NoteBlock(new Note(noteNames[i]), 400, 20, 100 + i * 70));
        }
        b.setNoteBlockListener(this);
        Tune.NoteEvent[] target = {
                new Tune.NoteEvent(new Note("C4"), 400, 100),
                new Tune.NoteEvent(new Note("E4"), 400, 100),
                new Tune.NoteEvent(new Note("G4"), 400, 100),
        };
        currentLevel.setTargetTune(new Tune(target));
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
        if (state == STATE_LEVEL1) {
            // Check palette for pick-up
            for (NoteBlock nb : palette) {
                if (nb.getBounds().contains(e.getX(), e.getY())) {
                    dragging = new NoteBlock(nb.getNote(), nb.getDurationMs(), e.getX()-25, e.getY()-25);
                    dragOffsetX = 25; dragOffsetY = 25;
                    return;
                }
            }
        }
        else if (state == STATE_INFO) {
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

    public void mouseDragged(MouseEvent e) {
        if (dragging != null) {
            dragging = new NoteBlock(dragging.getNote(), dragging.getDurationMs(),
                    e.getX() - dragOffsetX, e.getY() - dragOffsetY);
            window.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (dragging != null) {
            currentLevel.addObstacle(dragging); // place it
            b.setNoteBlockListener(this);        // re-register listener
            dragging = null;
            window.repaint();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (state >= 1.0) {
            b.tickStep(currentLevel);
            // Check if ball reached the target
            Rectangle targetRect = currentLevel.getTarget();
            Rectangle ballRect = new Rectangle((int)b.getXpos(), (int)b.getYpos(), Ball.SIZE, Ball.SIZE);

            if (ballRect.intersects(targetRect)) {
                Tune playerTune = tuneRecorder.buildTune();
                int score = currentLevel.getTargetTune().score(playerTune);
                if (score >= 95) {
                    state = STATE_WIN;
                } else {
                    state = STATE_SCORE_SCREEN; // show score, let them retry
                }
            }
        }
        window.repaint();
    }

    @Override
    public void onNoteBlockHit(NoteBlock block) {
        block.playNote();
        block.setInPlay(true);
        tuneRecorder.recordHit(block.getNote(), block.getDurationMs());
        new javax.swing.Timer(block.getDurationMs(), e2 -> {
            block.setInPlay(false);
            ((javax.swing.Timer) e2.getSource()).stop();
        }).start();
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    public static void main(String[] args) {
        Game g = new Game();
    }
}

