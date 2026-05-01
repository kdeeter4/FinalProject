import java.awt.Graphics;
import java.awt.*;

public class NoteBlock extends Obstacle{

    private Note note;
    private boolean inPlay;
    private double x;
    private double y;
    private int durationMs;
    public static final int RADIUS = 30;

    public NoteBlock(Note note, int durationMs, double x, double y) {
        super((int) x, (int) y, RADIUS * 2, RADIUS * 2, Color.GREEN);
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
            g2d.setColor(new Color(60, 200, 90));  // green while note is playing
        } else {
            g2d.setColor(Color.DARK_GRAY);  // idle
        }

        // Fill rounded block
        int diameter = RADIUS * 2;

// Fill circular block
        g2d.fillOval((int) x, (int) y, diameter, diameter);

// Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int) x, (int) y, diameter, diameter);

// Note name centered inside the block
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String label = note.getName();
        int textX = (int) x + (diameter - fm.stringWidth(label)) / 2;
        int textY = (int) y + (diameter - fm.getHeight()) / 2 + fm.getAscent();
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

    @Override
    public boolean isCircle() {
        return true;
    }

    @Override
    public double getCenterX() {
        return x + RADIUS;
    }

    @Override
    public double getCenterY() {
        return y + RADIUS;
    }

    @Override
    public double getRadius() {
        return RADIUS;
    }

    public double getBounce() {
        int semitone = Tune.toSemitone(note);
        semitone = Math.max(0, Math.min(83, semitone));
        return 0.45 + (semitone / 83.0) * 0.70;
    }

    public Note getNote() { return note; }
    public int getDurationMs() { return durationMs; }

    public void setInPlay(boolean inPlay) {
        this.inPlay = inPlay;
    }
}