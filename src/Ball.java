import java.awt.*;

public class Ball {
    private double xpos;
    private double ypos;
    private double dx;
    private double dy;

    private static final int SIZE = 35;
    private static final double GRAVITY = 1;
    private static final double BOUNCE = 0.9;

    public Ball(double x, double y) {
        xpos = x;
        ypos = y;
        dx = 30;
        dy = -20;
    }

    public void tickStep() {
        dy += GRAVITY;

        double nextX = xpos + dx;
        double nextY = ypos + dy;

        // Left wall
        if (nextX <= 0) {
            xpos = 0;
            dx = -dx;
        }
        // Right wall
        else if (nextX + SIZE >= 1000) {
            xpos = 1000 - SIZE;
            dx = -dx;
        }
        else {
            xpos = nextX;
        }

        // Ceiling
        if (nextY <= 0) {
            ypos = 0;
            dy = -dy * BOUNCE;
        }
        // Floor
        else if (nextY + SIZE >= 1000) {
            ypos = 1000 - SIZE;
            dy = -dy * BOUNCE;

            if (Math.abs(dy) < 1.0) {
                dy = 0;
            }
        }
        else {
            ypos = nextY;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) xpos, (int) ypos, SIZE, SIZE);
    }
}