import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameView extends JFrame {
    // Window dimensions
    public static final int LOGICAL_WIDTH  = 1000;
    public static final int LOGICAL_HEIGHT = 1000;
    public static final int WINDOW_WIDTH   = LOGICAL_WIDTH;
    public static final int WINDOW_HEIGHT  = LOGICAL_HEIGHT;

    // ? (help) button
    public static final int HELP_BTN_CX     = 920;
    public static final int HELP_BTN_CY     = 55;
    public static final int HELP_BTN_RADIUS = 22;

    // Level 1 box
    public static final int LEVEL1_X = 350;
    public static final int LEVEL1_Y = 380;
    public static final int LEVEL1_W = 300;
    public static final int LEVEL1_H = 100;

    // Instruction overlay
    public static final int OVERLAY_X = 150;
    public static final int OVERLAY_Y = 100;
    public static final int OVERLAY_W = 700;
    public static final int OVERLAY_H = 550;

    // Close (X) button on overlay
    public static final int CLOSE_BTN_X    = OVERLAY_X + OVERLAY_W - 42;
    public static final int CLOSE_BTN_Y    = OVERLAY_Y + 10;
    public static final int CLOSE_BTN_SIZE = 30;

    // Preview-tune button (below the clear button in the sidebar)
    public static final int PREVIEW_BTN_X = Game.CLEAR_BTN_X;
    public static final int PREVIEW_BTN_W = Game.CLEAR_BTN_W;
    public static final int PREVIEW_BTN_H = 36;

    // Shared retry / menu button on result screens
    public static final int RETRY_BTN_X = 350;
    public static final int RETRY_BTN_Y = 680;
    public static final int RETRY_BTN_W = 300;
    public static final int RETRY_BTN_H = 60;

    // Melody HUD
    private static final int HUD_Y          = 10;   // top of HUD strip
    private static final int HUD_CIRCLE_R   = 22;   // radius of each note circle
    private static final int HUD_SPACING    = 58;   // centre-to-centre spacing

    private Game      backend;
    private GamePanel panel;

    public GameView(Game backend) {
        this.backend = backend;
        setTitle("Melody Ball");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel = new GamePanel();
        panel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel getPanel() { return panel; }

    // ── Inner panel ───────────────────────────────────────────────────────────

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            double st = backend.getState();

            if (st == Game.STATE_WIN) {
                drawWinScreen(g, backend.getLastScore());
            } else if (st == Game.STATE_SCORE_SCREEN) {
                drawScoreScreen(g, backend.getLastScore());
            } else if (st >= Game.STATE_LEVEL1_SETUP) {
                drawLevel1(g);
            } else {
                drawLevelSelect(g, 0);
                if (st == Game.STATE_INFO) {
                    drawInstructionOverlay(g, 0);
                }
            }
        }
    }

    // ── Level select ──────────────────────────────────────────────────────────

    private void drawLevelSelect(Graphics g, int dy) {
        g.setColor(new Color(220, 230, 255));
        g.fillRect(0, dy, WINDOW_WIDTH, WINDOW_HEIGHT);

        g.setColor(new Color(30, 30, 80));
        g.setFont(new Font("Arial", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String title = "Select a Level";
        g.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, dy + 80);

        // ? button
        g.setColor(new Color(60, 120, 200));
        g.fillOval(HELP_BTN_CX - HELP_BTN_RADIUS, dy + HELP_BTN_CY - HELP_BTN_RADIUS,
                HELP_BTN_RADIUS * 2, HELP_BTN_RADIUS * 2);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        fm = g.getFontMetrics();
        g.drawString("?", HELP_BTN_CX - fm.stringWidth("?") / 2, dy + HELP_BTN_CY + 8);

        // Level 1 box
        g.setColor(new Color(80, 170, 90));
        g.fillRoundRect(LEVEL1_X, dy + LEVEL1_Y, LEVEL1_W, LEVEL1_H, 18, 18);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        fm = g.getFontMetrics();
        String lvl = "Level 1";
        g.drawString(lvl, LEVEL1_X + (LEVEL1_W - fm.stringWidth(lvl)) / 2,
                dy + LEVEL1_Y + LEVEL1_H / 2 + 10);
    }

    // ── Instruction overlay ───────────────────────────────────────────────────

    private void drawInstructionOverlay(Graphics g, int dy) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(0, 0, 0, 130));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(OVERLAY_X, dy + OVERLAY_Y, OVERLAY_W, OVERLAY_H, 20, 20);
        g2d.setColor(new Color(80, 80, 120));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(OVERLAY_X, dy + OVERLAY_Y, OVERLAY_W, OVERLAY_H, 20, 20);

        g.setColor(new Color(30, 30, 80));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        String title = "How to Play";
        g.drawString(title, OVERLAY_X + (OVERLAY_W - fm.stringWidth(title)) / 2, dy + OVERLAY_Y + 52);

        g.setColor(new Color(180, 180, 220));
        g.drawLine(OVERLAY_X + 30, dy + OVERLAY_Y + 65, OVERLAY_X + OVERLAY_W - 30, dy + OVERLAY_Y + 65);

        g.setColor(new Color(40, 40, 40));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] lines = {
                "Welcome to Melody Ball!",
                "",
                "  \u2022  Use your mouse to drag note blocks from the sidebar.",
                "  \u2022  Drop them onto the play field to act as platforms.",
                "  \u2022  The ball bounces off each block and plays its note.",
                "",
                "  \u2022  Match the target melody shown at the top of the screen.",
                "  \u2022  Get the ball to the green target area to finish.",
                "  \u2022  You need a melody score of 95+ to win!",
                "",
                "  \u2022  Use the Clear Board button to remove all placed blocks.",
                "",
                "Good luck!",
        };
        int textY = dy + OVERLAY_Y + 100;
        for (String line : lines) {
            g.drawString(line, OVERLAY_X + 40, textY);
            textY += 32;
        }

        // X (close) button
        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRoundRect(CLOSE_BTN_X, dy + CLOSE_BTN_Y, CLOSE_BTN_SIZE, CLOSE_BTN_SIZE, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        fm = g2d.getFontMetrics();
        g2d.drawString("X", CLOSE_BTN_X + (CLOSE_BTN_SIZE - fm.stringWidth("X")) / 2,
                dy + CLOSE_BTN_Y + CLOSE_BTN_SIZE - 9);
    }

    // ── Level 1 (play area) ───────────────────────────────────────────────────

    private void drawLevel1(Graphics g) {
        Level level = backend.getCurrentLevel();
        if (level != null) level.draw(g);

        backend.getBall().draw(g);

        // Draw drag ghost
        if (backend.getDragging() != null) {
            backend.getDragging().draw(g);
        }

        drawSidebar(g);
        drawMelodyHUD(g);

        // Setup-mode banner across the bottom of the play area
        if (backend.isSetupMode()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(0, 0, 0, 140));
            g2d.fillRoundRect(20, WINDOW_HEIGHT - 46, Game.SIDEBAR_X - 40, 36, 10, 10);
            g2d.setColor(new Color(180, 220, 255));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String hint = "SETUP  \u2014  Drag note blocks onto the field, then press \u25B6 PLAY";
            g2d.drawString(hint, 20 + (Game.SIDEBAR_X - 40 - fm.stringWidth(hint)) / 2,
                    WINDOW_HEIGHT - 46 + 23);
        }
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private void drawSidebar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean setup = backend.isSetupMode();
        int sx = Game.SIDEBAR_X;
        int sw = Game.SIDEBAR_W;

        // Background strip
        g2d.setColor(new Color(30, 30, 50));
        g2d.fillRect(sx, 0, sw, WINDOW_HEIGHT);

        if (setup) {
            // "PLACE NOTES" label at the top of the palette area
            g2d.setColor(new Color(180, 200, 255));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g2d.getFontMetrics();
            String lbl = "PLACE";
            g2d.drawString(lbl, sx + (sw - fm.stringWidth(lbl)) / 2, WINDOW_HEIGHT - 55);
            String lbl2 = "NOTES";
            g2d.drawString(lbl2, sx + (sw - fm.stringWidth(lbl2)) / 2, WINDOW_HEIGHT - 43);

            // Palette slots — only interactive in setup
            java.util.List<NoteBlock> palette = backend.getPalette();
            for (int i = 0; i < palette.size(); i++) {
                int slotY = Game.SIDEBAR_PAD + i * Game.SIDEBAR_SLOT_H;
                g2d.setColor(new Color(50, 55, 80));
                g2d.fillRoundRect(sx + 4, slotY, sw - 8, Game.SIDEBAR_SLOT_H - 4, 10, 10);
                palette.get(i).draw(g);
            }

            // Clear board button
            int clearY = backend.getClearBtnY();
            g2d.setColor(new Color(180, 50, 50));
            g2d.fillRoundRect(Game.CLEAR_BTN_X, clearY, Game.CLEAR_BTN_W, Game.CLEAR_BTN_H, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String clr = "CLEAR";
            g2d.drawString(clr, Game.CLEAR_BTN_X + (Game.CLEAR_BTN_W - fm.stringWidth(clr)) / 2,
                    clearY + Game.CLEAR_BTN_H / 2 + 4);

            // Preview button
            int previewY = backend.getPreviewBtnY();
            boolean playing = backend.isPreviewPlaying();
            g2d.setColor(playing ? new Color(80, 200, 200) : new Color(50, 120, 170));
            g2d.fillRoundRect(PREVIEW_BTN_X, previewY, PREVIEW_BTN_W, PREVIEW_BTN_H, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String prev = playing ? "PLAY\u2026" : "\u25B6 HEAR";
            g2d.drawString(prev, PREVIEW_BTN_X + (PREVIEW_BTN_W - fm.stringWidth(prev)) / 2,
                    previewY + PREVIEW_BTN_H / 2 + 4);

            // ▶ PLAY button — big green, bottom of setup controls
            int playY = backend.getPlayBtnY();
            g2d.setColor(new Color(40, 180, 80));
            g2d.fillRoundRect(Game.CLEAR_BTN_X, playY, Game.CLEAR_BTN_W, Game.CLEAR_BTN_H + 4, 8, 8);
            // inner highlight
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRoundRect(Game.CLEAR_BTN_X, playY, Game.CLEAR_BTN_W, (Game.CLEAR_BTN_H + 4) / 2, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            fm = g2d.getFontMetrics();
            String play = "\u25B6 PLAY";
            g2d.drawString(play, Game.CLEAR_BTN_X + (Game.CLEAR_BTN_W - fm.stringWidth(play)) / 2,
                    playY + (Game.CLEAR_BTN_H + 4) / 2 + 5);

        } else {
            // Live play — sidebar is locked, just show a dim note list for reference
            java.util.List<NoteBlock> palette = backend.getPalette();
            for (int i = 0; i < palette.size(); i++) {
                int slotY = Game.SIDEBAR_PAD + i * Game.SIDEBAR_SLOT_H;
                g2d.setColor(new Color(38, 40, 60));   // dimmer slot
                g2d.fillRoundRect(sx + 4, slotY, sw - 8, Game.SIDEBAR_SLOT_H - 4, 10, 10);
                palette.get(i).draw(g);
            }
            // Preview button still works during play
            int previewY = backend.getPreviewBtnY();
            boolean playing = backend.isPreviewPlaying();
            g2d.setColor(playing ? new Color(80, 200, 200) : new Color(50, 120, 170));
            g2d.fillRoundRect(PREVIEW_BTN_X, previewY, PREVIEW_BTN_W, PREVIEW_BTN_H, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2d.getFontMetrics();
            String prev = playing ? "PLAY\u2026" : "\u25B6 HEAR";
            g2d.drawString(prev, PREVIEW_BTN_X + (PREVIEW_BTN_W - fm.stringWidth(prev)) / 2,
                    previewY + PREVIEW_BTN_H / 2 + 4);

            // Restart button — only shown while ball is live
            int restartY = backend.getRestartBtnY();
            g2d.setColor(new Color(220, 140, 40));
            g2d.fillRoundRect(Game.CLEAR_BTN_X, restartY, Game.CLEAR_BTN_W, Game.CLEAR_BTN_H, 8, 8);
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillRoundRect(Game.CLEAR_BTN_X, restartY, Game.CLEAR_BTN_W, Game.CLEAR_BTN_H / 2, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String rst = "\u21BA RESET";
            g2d.drawString(rst, Game.CLEAR_BTN_X + (Game.CLEAR_BTN_W - fm.stringWidth(rst)) / 2,
                    restartY + Game.CLEAR_BTN_H / 2 + 4);

            // "BALL LIVE" indicator
            g2d.setColor(new Color(80, 200, 200));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            fm = g2d.getFontMetrics();
            String live = "BALL";
            g2d.drawString(live, sx + (sw - fm.stringWidth(live)) / 2, WINDOW_HEIGHT - 55);
            String live2 = "LIVE";
            g2d.drawString(live2, sx + (sw - fm.stringWidth(live2)) / 2, WINDOW_HEIGHT - 43);
        }
    }

    // ── Melody HUD ────────────────────────────────────────────────────────────

    private void drawMelodyHUD(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Level level = backend.getCurrentLevel();
        if (level == null || level.getTargetTune() == null) return;

        Tune.NoteEvent[] target  = level.getTargetTune().getEvents();
        Tune.NoteEvent[] played  = backend.getTuneRecorder().buildTune().getEvents();

        int n        = target.length;
        int totalW   = n * HUD_SPACING;
        int startX   = (Game.SIDEBAR_X - totalW) / 2 + HUD_SPACING / 2; // centred in play area
        int cy       = HUD_Y + HUD_CIRCLE_R + 4;

        // HUD backdrop
        int padX = 16, padY = 8;
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRoundRect(startX - HUD_CIRCLE_R - padX, HUD_Y - padY,
                totalW + padX * 2, HUD_CIRCLE_R * 2 + padY * 2 + 18, 14, 14);

        // Label above circles
        g2d.setColor(new Color(200, 220, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        String lbl = "TARGET MELODY";
        g2d.drawString(lbl, startX - HUD_CIRCLE_R - padX + (totalW + padX * 2 - fm.stringWidth(lbl)) / 2,
                HUD_Y - padY + 13);

        for (int i = 0; i < n; i++) {
            int cx = startX + i * HUD_SPACING;

            // Determine fill colour
            Color fill;
            if (i < played.length) {
                // Compare semitone — green if exact match, amber if close, red if wrong
                int semiTarget = toSemitone(target[i].note);
                int semiPlayed = toSemitone(played[i].note);
                int diff = Math.abs(semiTarget - semiPlayed);
                if (diff == 0)       fill = new Color(60, 200, 90);   // exact — green
                else if (diff <= 2)  fill = new Color(230, 180, 40);  // close — amber
                else                 fill = new Color(210, 60, 60);   // wrong — red
            } else {
                fill = new Color(70, 75, 110);   // not yet played — dark slate
            }

            // Circle
            g2d.setColor(fill);
            g2d.fillOval(cx - HUD_CIRCLE_R, cy - HUD_CIRCLE_R, HUD_CIRCLE_R * 2, HUD_CIRCLE_R * 2);

            // Border ring — brighter for next-to-play
            if (i == played.length) {
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.5f));
            } else {
                g2d.setColor(new Color(120, 130, 180));
                g2d.setStroke(new BasicStroke(1.5f));
            }
            g2d.drawOval(cx - HUD_CIRCLE_R, cy - HUD_CIRCLE_R, HUD_CIRCLE_R * 2, HUD_CIRCLE_R * 2);
            g2d.setStroke(new BasicStroke(1));

            // Note label inside circle
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            fm = g2d.getFontMetrics();
            String noteName = target[i].note.getName();
            g2d.drawString(noteName, cx - fm.stringWidth(noteName) / 2, cy + fm.getAscent() / 2 - 1);
        }
    }

    // Converts a Note to absolute semitone (C4 = 48, etc.)
    private int toSemitone(Note note) {
        int[] semitones  = {0, 2, 4, 5, 7, 9, 11};
        String noteOrder = "CDEFGAB";
        int idx = noteOrder.indexOf(note.letter);
        return note.octave * 12 + (idx >= 0 ? semitones[idx] : 0);
    }

    // ── Win screen ────────────────────────────────────────────────────────────

    private void drawWinScreen(Graphics g, int score) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Deep green gradient background
        GradientPaint grad = new GradientPaint(0, 0, new Color(10, 60, 20),
                0, WINDOW_HEIGHT, new Color(20, 120, 50));
        g2d.setPaint(grad);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Decorative star-burst circles
        g2d.setColor(new Color(255, 255, 100, 30));
        for (int i = 0; i < 6; i++) {
            int r = 80 + i * 55;
            g2d.fillOval(WINDOW_WIDTH / 2 - r, WINDOW_HEIGHT / 2 - r, r * 2, r * 2);
        }

        // "YOU WIN!" text
        g2d.setColor(new Color(255, 230, 60));
        g2d.setFont(new Font("Arial", Font.BOLD, 88));
        FontMetrics fm = g2d.getFontMetrics();
        String win = "YOU WIN!";
        g2d.drawString(win, (WINDOW_WIDTH - fm.stringWidth(win)) / 2, 320);

        // Score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        fm = g2d.getFontMetrics();
        String scoreLine = "Melody Score: " + score + " / 100";
        g2d.drawString(scoreLine, (WINDOW_WIDTH - fm.stringWidth(scoreLine)) / 2, 420);

        // Perfect badge
        g2d.setColor(new Color(255, 230, 60));
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        fm = g2d.getFontMetrics();
        String perf = "\u2605 Perfect Melody! \u2605";
        g2d.drawString(perf, (WINDOW_WIDTH - fm.stringWidth(perf)) / 2, 480);

        // Menu button
        drawResultButton(g2d, "Back to Menu", false);
    }

    // ── Score / retry screen ──────────────────────────────────────────────────

    private void drawScoreScreen(Graphics g, int score) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Warm dark background
        GradientPaint grad = new GradientPaint(0, 0, new Color(50, 20, 10),
                0, WINDOW_HEIGHT, new Color(100, 40, 20));
        g2d.setPaint(grad);
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Title
        g2d.setColor(new Color(255, 160, 60));
        g2d.setFont(new Font("Arial", Font.BOLD, 68));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "Not Quite!";
        g2d.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, 280);

        // Score number — coloured by quality
        Color scoreColor = score >= 75 ? new Color(255, 220, 60)
                : score >= 50 ? new Color(255, 140, 40)
                :               new Color(230, 70, 70);
        g2d.setColor(scoreColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 110));
        fm = g2d.getFontMetrics();
        String scoreStr = String.valueOf(score);
        g2d.drawString(scoreStr, (WINDOW_WIDTH - fm.stringWidth(scoreStr)) / 2, 420);

        // "/ 100" label
        g2d.setColor(new Color(200, 180, 160));
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g2d.getFontMetrics();
        String outOf = "out of 100  (need 95 to win)";
        g2d.drawString(outOf, (WINDOW_WIDTH - fm.stringWidth(outOf)) / 2, 475);

        // Encouragement line
        g2d.setColor(new Color(255, 200, 140));
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g2d.getFontMetrics();
        String enc = score >= 75 ? "So close! Adjust your note placement."
                : score >= 50 ? "Getting there! Try matching the target melody."
                :               "Keep experimenting with the note blocks!";
        g2d.drawString(enc, (WINDOW_WIDTH - fm.stringWidth(enc)) / 2, 530);

        // Retry button
        drawResultButton(g2d, "Try Again", true);
    }

    /** Draws the shared action button on result screens. */
    private void drawResultButton(Graphics2D g2d, String label, boolean isRetry) {
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(RETRY_BTN_X + 4, RETRY_BTN_Y + 4, RETRY_BTN_W, RETRY_BTN_H, 16, 16);

        // Button fill
        Color btnColor = isRetry ? new Color(60, 140, 230) : new Color(60, 180, 90);
        g2d.setColor(btnColor);
        g2d.fillRoundRect(RETRY_BTN_X, RETRY_BTN_Y, RETRY_BTN_W, RETRY_BTN_H, 16, 16);

        // Highlight top edge
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.fillRoundRect(RETRY_BTN_X, RETRY_BTN_Y, RETRY_BTN_W, RETRY_BTN_H / 2, 16, 16);

        // Border
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(RETRY_BTN_X, RETRY_BTN_Y, RETRY_BTN_W, RETRY_BTN_H, 16, 16);
        g2d.setStroke(new BasicStroke(1));

        // Label
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label,
                RETRY_BTN_X + (RETRY_BTN_W - fm.stringWidth(label)) / 2,
                RETRY_BTN_Y + RETRY_BTN_H / 2 + fm.getAscent() / 2 - 2);
    }
}