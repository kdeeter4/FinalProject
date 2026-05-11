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
    public static final double STATE_INFO         = -1.0;
    public static final double STATE_MENU         =  0.0;
    public static final double STATE_LEVEL1_SETUP =  1.00;  // placing blocks, ball is still
    public static final double STATE_LEVEL1       =  1.01;  // ball is live
    public static final double STATE_LEVEL2       =  1.02;
    public static final double STATE_WIN          =  2.0;
    public static final double STATE_SCORE_SCREEN =  3.0;

    // Sidebar geometry (shared with GameView for hit-testing)
    public static final int SIDEBAR_X    = 720;
    public static final int SIDEBAR_W    = 80;
    public static final int SIDEBAR_SLOT_H = 88;
    public static final int SIDEBAR_PAD    = 10;

    // Clear-board button
    public static final int CLEAR_BTN_X = SIDEBAR_X + 5;
    public static final int CLEAR_BTN_W = SIDEBAR_W - 10;
    public static final int CLEAR_BTN_H = 36;

    // Win percent
    public static final int MIN_WIN = 85;

    // Instance variables
    private GameView        window;
    private double          state;
    private Ball            b;
    private Level           currentLevel;
    private NoteBlock       dragging     = null;
    private int             dragOffsetX, dragOffsetY;
    private List<NoteBlock> palette;
    private TuneRecorder    tuneRecorder;
    private int             lastScore = 0;
    private boolean         previewPlaying = false;

    private static final double BALL_START_X = 250.0;
    private static final double BALL_START_Y =  50.0;

    // Constructor
    public Game() {
        window       = new GameView(this);
        state        = STATE_INFO;
        tuneRecorder = new TuneRecorder();

        window.getPanel().addMouseListener(this);
        window.getPanel().addMouseMotionListener(this);

        new Timer(16, this).start();

        palette = new ArrayList<>();
    }
    // Getters
    public double          getState()        { return state; }
    public Ball            getBall()         { return b; }
    public Level           getCurrentLevel() { return currentLevel; }
    public List<NoteBlock> getPalette()      { return palette; }
    public int             getLastScore()    { return lastScore; }
    public NoteBlock       getDragging()     { return dragging; }
    public boolean         isSetupMode()     { return state == STATE_LEVEL1_SETUP; }

    // Clear button coordinates
    public int getClearBtnY()   { return SIDEBAR_PAD + palette.size() * SIDEBAR_SLOT_H + SIDEBAR_PAD; }

    // Y coordinate of the preview button — sits directly below the clear button.
    public int getPreviewBtnY() { return getClearBtnY()   + CLEAR_BTN_H + SIDEBAR_PAD; }

    // Y coordinate of the Play button — sits below the preview button.
    public int getPlayBtnY()    { return getPreviewBtnY() + CLEAR_BTN_H + SIDEBAR_PAD; }

    // Y coordinate of the Restart button — sits below the play button.
    public int getRestartBtnY() { return getPlayBtnY()    + CLEAR_BTN_H + SIDEBAR_PAD; }

    private void loadLevel(Level level) {
        currentLevel = level;
        tuneRecorder = new TuneRecorder();
        b = new Ball(currentLevel.getBallSpawnX(), currentLevel.getBallSpawnY());
        b.setNoteBlockListener(this);

        // Build sidebar palette with correct on-screen x position
        palette = new ArrayList<>();
        List<Note> notes = currentLevel.getPaletteNotes();
        for (int i = 0; i < notes.size(); i++) {
            int slotY = SIDEBAR_PAD + i * SIDEBAR_SLOT_H;
            palette.add(new NoteBlock(notes.get(i), 400,
                    SIDEBAR_X + SIDEBAR_PAD, slotY + SIDEBAR_PAD));
        }
    }

    private void resetCurrentLevel() {
        if (currentLevel == null) {
            return;
        }

        currentLevel.clearPlacedBlocks();
        tuneRecorder = new TuneRecorder();

        b = new Ball(currentLevel.getBallSpawnX(), currentLevel.getBallSpawnY());
        b.setNoteBlockListener(this);

        // Rebuild sidebar palette at correct x position
        palette = new ArrayList<>();
        List<Note> notes = currentLevel.getPaletteNotes();
        for (int i = 0; i < notes.size(); i++) {
            int slotY = SIDEBAR_PAD + i * SIDEBAR_SLOT_H;
            palette.add(new NoteBlock(notes.get(i), 400,
                    SIDEBAR_X + SIDEBAR_PAD, slotY + SIDEBAR_PAD));
        }
    }

    // Checks if it is playing
    public boolean isPreviewPlaying() { return previewPlaying; }

    // Mouse code
    @Override
    public void mousePressed(MouseEvent e) {
        int x = toLogical(e.getX()), y = toLogical(e.getY());

        // Setup and live play share the same sidebar/palette interactions
        if (state == STATE_LEVEL1_SETUP || state == STATE_LEVEL1) {
            // Clear-board button (only in setup)
            if (state == STATE_LEVEL1_SETUP) {
                int clearY = getClearBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= clearY && y <= clearY + CLEAR_BTN_H) {
                    clearBoard();
                    return;
                }
            }

            // Preview-tune button (only in setup)
            if (state == STATE_LEVEL1_SETUP) {
                int previewY = getPreviewBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= previewY && y <= previewY + CLEAR_BTN_H) {
                    previewTargetTune();
                    return;
                }
            }

            // Play button (only visible in setup mode)
            if (state == STATE_LEVEL1_SETUP) {
                int playY = getPlayBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= playY && y <= playY + CLEAR_BTN_H) {
                    state = STATE_LEVEL1;
                    window.repaint();
                    return;
                }
            }

            // Restart button (only visible while ball is live)
            if (state == STATE_LEVEL1) {
                int restartY = getRestartBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= restartY && y <= restartY + CLEAR_BTN_H) {
                    b.reset();
                    tuneRecorder.reset();
                    state = STATE_LEVEL1_SETUP;
                    window.repaint();
                    return;
                }
            }

            // Pick up an already-placed NoteBlock (only in setup)
            if (state == STATE_LEVEL1_SETUP) {
                List<Obstacle> placed = currentLevel.getPlacedBlocks();
                for (int i = placed.size() - 1; i >= 0; i--) {
                    Obstacle o = placed.get(i);
                    if (o instanceof NoteBlock && o.getBounds().contains(x, y)) {
                        NoteBlock nb = (NoteBlock) o;
                        currentLevel.removePlacedBlock(i);   // see note below
                        dragging = new NoteBlock(nb.getNote(), nb.getDurationMs(), x - 25, y - 25);
                        dragOffsetX = 25;
                        dragOffsetY = 25;
                        return;
                    }
                }
            }

            // Palette pick-up (only in setup — can't rearrange mid-flight)
            if (state == STATE_LEVEL1_SETUP) {
                for (NoteBlock nb : palette) {
                    if (nb.getBounds().contains(x, y)) {
                        dragging    = new NoteBlock(nb.getNote(), nb.getDurationMs(), x - 25, y - 25);
                        dragOffsetX = 25;
                        dragOffsetY = 25;
                        return;
                    }
                }
            }
        // X button
        } else if (state == STATE_INFO) {
            if (x >= GameView.CLOSE_BTN_X && x <= GameView.CLOSE_BTN_X + GameView.CLOSE_BTN_SIZE
                    && y >= GameView.CLOSE_BTN_Y && y <= GameView.CLOSE_BTN_Y + GameView.CLOSE_BTN_SIZE) {
                state = STATE_MENU;
                window.repaint();
            }
        // Help button + Level 1 button
        } else if (state == STATE_MENU) {
            int ddx = x - GameView.HELP_BTN_CX, ddy = y - GameView.HELP_BTN_CY;
            if (Math.sqrt(ddx*ddx + ddy*ddy) <= GameView.HELP_BTN_RADIUS) {
                state = STATE_INFO; window.repaint(); return;
            }
            if (x >= GameView.LEVEL1_X && x <= GameView.LEVEL1_X + GameView.LEVEL1_W
                    && y >= GameView.LEVEL1_Y && y <= GameView.LEVEL1_Y + GameView.LEVEL1_H) {
                loadLevel(Level.makeLevel1());
                state = STATE_LEVEL1_SETUP;
                window.repaint();
            }
        // Retry button
        } else if (state == STATE_SCORE_SCREEN) {
            if (x >= GameView.RETRY_BTN_X && x <= GameView.RETRY_BTN_X + GameView.RETRY_BTN_W
                    && y >= GameView.RETRY_BTN_Y && y <= GameView.RETRY_BTN_Y + GameView.RETRY_BTN_H) {
                retryLevel();
            }
        // Return button
        } else if (state == STATE_WIN) {
            if (x >= GameView.RETRY_BTN_X && x <= GameView.RETRY_BTN_X + GameView.RETRY_BTN_W
                    && y >= GameView.RETRY_BTN_Y && y <= GameView.RETRY_BTN_Y + GameView.RETRY_BTN_H) {
                state = STATE_MENU; window.repaint();
            }
        }
    }

    // Creates a new noteblock when dragged
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging != null) {
            dragging = new NoteBlock(dragging.getNote(), dragging.getDurationMs(),
                    toLogical(e.getX()) - dragOffsetX, toLogical(e.getY()) - dragOffsetY);
            window.repaint();
        }
    }

    // Adds the obstacle to the position
    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging != null) {
            if (toLogical(e.getX()) < SIDEBAR_X) {   // only place inside the play area
                currentLevel.addPlacedNoteBlock(dragging);;
                b.setNoteBlockListener(this);
            }
            dragging = null;
            window.repaint();
        }
    }

    // Game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == STATE_LEVEL1) {   // only tick physics when ball is live
            b.tickStep(currentLevel);
            Rectangle ballRect = new Rectangle((int)b.getXpos(), (int)b.getYpos(), Ball.SIZE, Ball.SIZE);
            if (ballRect.intersects(currentLevel.getTarget())) {
                lastScore = currentLevel.getTargetTune().score(tuneRecorder.buildTune());
                state = (lastScore >= 85) ? STATE_WIN : STATE_SCORE_SCREEN;
            } else if (b.isOutOfBounds()) {
                // Ball fell off the screen — reset like the restart button
                b.reset();
                tuneRecorder.reset();
                state = STATE_LEVEL1_SETUP;
            }
        }
        window.repaint();
    }

    // Noteblock listener
    @Override
    public void onNoteBlockHit(NoteBlock block) {
        // Don't replay if it is already playing
        if (block.isInPlay()) return;
        // Plays note and adds it to the 'recording'
        block.playNote();
        block.setInPlay(true);
        tuneRecorder.recordHit(block.getNote(), block.getDurationMs());
        new javax.swing.Timer(block.getDurationMs(), ev -> {
            block.setInPlay(false);
            ((javax.swing.Timer) ev.getSource()).stop();
        }).start();
    }

    // Clears the board of all the notebocks
    private void clearBoard() {
        currentLevel.clearPlacedBlocks();
        tuneRecorder.reset();
        window.repaint();
    }

    // Plays the tune so that the user can hear what it is supposed to sound like
    private void previewTargetTune() {
        if (previewPlaying) return;   // ignore if already playing
        Tune target = currentLevel.getTargetTune();
        if (target == null) return;

        previewPlaying = true;
        window.repaint();

        // New thread so it doesn't interrupt anything
        Thread t = new Thread(() -> {
            try {
                for (Tune.NoteEvent ev : target.getEvents()) {
                    ev.note.playNote(ev.durationMs);
                    Thread.sleep(ev.durationMs + ev.gapMs);
                }
            } catch (InterruptedException ignored) {
            } finally {
                previewPlaying = false;
                window.repaint();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // Reset the level
    public void retryLevel() {
        resetCurrentLevel();
        state = STATE_LEVEL1_SETUP;
        window.repaint();
    }

    // Converts a screen mouse coordinate to logical 1000x1000 space
    private int toLogical(int screenCoord) {
        return (int)(screenCoord / GameView.SCALE);
    }

    // Expose recorder so GameView can read played notes for HUD
    public TuneRecorder getTuneRecorder() { return tuneRecorder; }

    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
    public void mouseMoved(MouseEvent e)    {}
    public void mouseClicked(MouseEvent e)  {}

    public static void main(String[] args) { new Game(); }
}