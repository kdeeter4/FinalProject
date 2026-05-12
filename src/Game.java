
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
    public static final double STATE_LEVEL1_SETUP = 1.00;
    public static final double STATE_LEVEL1       = 1.01;
    public static final double STATE_LEVEL2_SETUP = 2.00;
    public static final double STATE_LEVEL2       = 2.01;
    public static final double STATE_LEVEL3_SETUP = 3.00;
    public static final double STATE_LEVEL3       = 3.01;
    public static final double STATE_WIN          = 4.0;
    public static final double STATE_SCORE_SCREEN = 5.0;

    // Sidebar geometry (shared with GameView for hit-testing)
    public static final int SIDEBAR_X    = 720;
    public static final int SIDEBAR_W    = 80;
    public static final int SIDEBAR_SLOT_H = 80;
    public static final int SIDEBAR_PAD    = 10;

    // Clear-board button
    public static final int CLEAR_BTN_X = SIDEBAR_X + 5;
    public static final int CLEAR_BTN_W = SIDEBAR_W - 10;
    public static final int CLEAR_BTN_H = 36;

    // Exit-to-menu button (top-left of play area, always visible during a level)
    public static final int EXIT_BTN_X = 10;
    public static final int EXIT_BTN_Y = 10;
    public static final int EXIT_BTN_W = 70;
    public static final int EXIT_BTN_H = 30;

    // Win percent
    public static final int MIN_WIN = 80;

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
    public boolean isSetupMode() {
        return state == STATE_LEVEL1_SETUP
                || state == STATE_LEVEL2_SETUP
                || state == STATE_LEVEL3_SETUP;
    }
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
        if (state == STATE_LEVEL1_SETUP || state == STATE_LEVEL1
                || state == STATE_LEVEL2_SETUP || state == STATE_LEVEL2
                || state == STATE_LEVEL3_SETUP || state == STATE_LEVEL3) {
            // Exit-to-menu button (always available during a level)
            if (x >= EXIT_BTN_X && x <= EXIT_BTN_X + EXIT_BTN_W
                    && y >= EXIT_BTN_Y && y <= EXIT_BTN_Y + EXIT_BTN_H) {
                state = STATE_MENU;
                window.repaint();
                return;
            }

            // Clear-board button (only in setup)
            if (isSetupMode()) {
                int clearY = getClearBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= clearY && y <= clearY + CLEAR_BTN_H) {
                    clearBoard();
                    return;
                }
            }

            // Preview-tune button (only in setup)
            if (isSetupMode()) {
                int previewY = getPreviewBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= previewY && y <= previewY + CLEAR_BTN_H) {
                    previewTargetTune();
                    return;
                }
            }

            // Play button (only visible in setup mode)
            if (isSetupMode()) {
                int playY = getPlayBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= playY && y <= playY + CLEAR_BTN_H) {
                    if (state == STATE_LEVEL2_SETUP) state = STATE_LEVEL2;
                    else if (state == STATE_LEVEL3_SETUP) state = STATE_LEVEL3;
                    else state = STATE_LEVEL1;
                    window.repaint();
                    return;
                }
            }

            // Restart button (only visible while ball is live)
            if (state == STATE_LEVEL1 || state == STATE_LEVEL2 || state == STATE_LEVEL3) {
                int restartY = getRestartBtnY();
                if (x >= CLEAR_BTN_X && x <= CLEAR_BTN_X + CLEAR_BTN_W
                        && y >= restartY && y <= restartY + CLEAR_BTN_H) {
                    b.reset();
                    tuneRecorder.reset();
                    if (state == STATE_LEVEL2) state = STATE_LEVEL2_SETUP;
                    else if (state == STATE_LEVEL3) state = STATE_LEVEL3_SETUP;
                    else state = STATE_LEVEL1_SETUP;
                    window.repaint();
                    return;
                }
            }

            // Pick up an already-placed NoteBlock (only in setup)
            if (isSetupMode()) {
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
            if (isSetupMode()) {
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
            if (x >= GameView.LEVEL2_X && x <= GameView.LEVEL2_X + GameView.LEVEL2_W
                    && y >= GameView.LEVEL2_Y && y <= GameView.LEVEL2_Y + GameView.LEVEL2_H) {
                loadLevel(Level.makeLevel2());
                state = STATE_LEVEL2_SETUP;
                window.repaint();
            }
            if (x >= GameView.LEVEL3_X && x <= GameView.LEVEL3_X + GameView.LEVEL3_W
                    && y >= GameView.LEVEL3_Y && y <= GameView.LEVEL3_Y + GameView.LEVEL3_H) {
                loadLevel(Level.makeLevel3());
                state = STATE_LEVEL3_SETUP;
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
        boolean ballLive = state == STATE_LEVEL1 || state == STATE_LEVEL2 || state == STATE_LEVEL3;
        if (ballLive) {
            b.tickStep(currentLevel);
            Rectangle ballRect = new Rectangle((int)b.getXpos(), (int)b.getYpos(), Ball.SIZE, Ball.SIZE);
            if (ballRect.intersects(currentLevel.getTarget())) {
                lastScore = currentLevel.getTargetTune().score(tuneRecorder.buildTune());
                state = (lastScore >= MIN_WIN) ? STATE_WIN : STATE_SCORE_SCREEN;
            } else if (b.isOutOfBounds()) {
                b.reset();
                tuneRecorder.reset();
                // Return to the setup state for whichever level is loaded
                if (state == STATE_LEVEL2) state = STATE_LEVEL2_SETUP;
                else if (state == STATE_LEVEL3) state = STATE_LEVEL3_SETUP;
                else state = STATE_LEVEL1_SETUP;
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

    // Reset the level — keeps all placed note blocks, just resets the ball and recorder
    public void retryLevel() {
        tuneRecorder = new TuneRecorder();
        b = new Ball(currentLevel.getBallSpawnX(), currentLevel.getBallSpawnY());
        b.setNoteBlockListener(this);
        state = currentLevelSetupState();
        window.repaint();
    }

    private double currentLevelSetupState() {
        if (currentLevel == null) return STATE_LEVEL1_SETUP;
        switch (currentLevel.getLevelNumber()) {
            case 2: return STATE_LEVEL2_SETUP;
            case 3: return STATE_LEVEL3_SETUP;
            default: return STATE_LEVEL1_SETUP;
        }
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


