import java.awt.*;
import javax.swing.*;

public class GameView extends JFrame {

    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 1000;

    // ? (help) button — circle center and radius (logical coords, no title bar)
    public static final int HELP_BTN_CX = 920;
    public static final int HELP_BTN_CY = 55;
    public static final int HELP_BTN_RADIUS = 22;

    // Level 1 box (logical coords)
    public static final int LEVEL1_X = 350;
    public static final int LEVEL1_Y = 380;
    public static final int LEVEL1_W = 300;
    public static final int LEVEL1_H = 100;

    // Instruction overlay box (logical coords)
    public static final int OVERLAY_X = 150;
    public static final int OVERLAY_Y = 100;
    public static final int OVERLAY_W = 700;
    public static final int OVERLAY_H = 550;

    // X (close) button on overlay — top-right corner of overlay box
    public static final int CLOSE_BTN_X = OVERLAY_X + OVERLAY_W - 42;
    public static final int CLOSE_BTN_Y = OVERLAY_Y + 10;
    public static final int CLOSE_BTN_SIZE = 30;

    private Game backend;

    public GameView(Game backend) {
        this.backend = backend;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Game");
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setVisible(true);
    }


    @Override
    public void paint(Graphics g) {
        backend.getBall().draw(g);
        int dy = getInsets().top; // offset so logical coords skip the title bar

        drawLevelSelect(g, dy);

        if (backend.getState() == -1) {
            drawInstructionOverlay(g, dy);
        }
    }

    private void drawLevelSelect(Graphics g, int dy) {
        // Background
        g.setColor(new Color(220, 230, 255));
        g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Title
        g.setColor(new Color(30, 30, 80));
        g.setFont(new Font("Arial", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        String title = "Select a Level";
        g.drawString(title, (WINDOW_WIDTH - fm.stringWidth(title)) / 2, dy + 80);

        // ? button circle
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

    private void drawInstructionOverlay(Graphics g, int dy) {
        // Graphics 2d object essentially extends the graphics object for more tools to put the overlay on
        Graphics2D g2d = (Graphics2D) g;

        // Dim the background
        g2d.setColor(new Color(0, 0, 0, 130));
        g2d.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Overlay box background
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(OVERLAY_X, dy + OVERLAY_Y, OVERLAY_W, OVERLAY_H, 20, 20);
        g2d.setColor(new Color(80, 80, 120));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(OVERLAY_X, dy + OVERLAY_Y, OVERLAY_W, OVERLAY_H, 20, 20);

        // Overlay title
        g.setColor(new Color(30, 30, 80));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics fm = g.getFontMetrics();
        String title = "How to Play";
        g.drawString(title, OVERLAY_X + (OVERLAY_W - fm.stringWidth(title)) / 2,
                dy + OVERLAY_Y + 52);

        // Divider line under title
        g.setColor(new Color(180, 180, 220));
        g.drawLine(OVERLAY_X + 30, dy + OVERLAY_Y + 65,
                OVERLAY_X + OVERLAY_W - 30, dy + OVERLAY_Y + 65);

        // Placeholder instruction text
        g.setColor(new Color(40, 40, 40));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] lines = {
            "Welcome to the game!",
            "",
            "  \u2022  Use your mouse to interact with the game.",
            "  \u2022  Click on a level to begin playing.",
            "  \u2022  Your goal is to complete each level.",
            "",
            "  \u2022  [Placeholder instruction line 1]",
            "  \u2022  [Placeholder instruction line 2]",
            "  \u2022  [Placeholder instruction line 3]",
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
}