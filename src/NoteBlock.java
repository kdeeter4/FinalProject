import java.awt.Graphics;
import java.awt.*;

public class NoteBlock extends Obstacle{

    private Note note;
    private boolean inPlay;
    private double x;
    private double y;
    private int durationMs;
    public int RADIUS = 25;

    public NoteBlock(Note note, int durationMs, double x, double y) {
        super((int) x,(int) y, 50, 50, Color.GREEN);
        this.note = note;
        this.durationMs = durationMs;
        this.x = x;
        this.y = y;
        this.inPlay = false;
    }

    public void playNote() {
        Thread audioThread = new Thread(() -> {
            note.playNote(durationMs);
            try {
                Thread.sleep(durationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        audioThread.setDaemon(true);
        audioThread.start();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (inPlay) {
            g2d.setColor(Color.YELLOW);  // lit up while playing
        } else {
            g2d.setColor(Color.DARK_GRAY);  // idle
        }

        // Fill rounded block
        g2d.fillRoundRect((int) x, (int) y, 50, 50, 15, 15);

        // Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect((int) x, (int) y, 50, 50, 15, 15);

        // Note name centered inside the block
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String label = note.getName();
        int textX = (int) x + (50 - fm.stringWidth(label)) / 2;
        int textY = (int) y + (50 - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(label, textX, textY);
    }

    public boolean isInPlay() {
        return inPlay;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getCenterX() { return x + RADIUS; }
    public double getCenterY() { return y + RADIUS; }
    public int getRadius() { return RADIUS; }
    public Note getNote() { return note; }
    public int getDurationMs() { return durationMs; }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }
}