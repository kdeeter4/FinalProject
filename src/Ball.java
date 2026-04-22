import java.awt.*;

public class Ball {
    private double xpos;
    private double ypos;
    private double dx;
    private double dy;

    public Ball(double x, double y) {
        xpos = x;
        ypos = y;
        dx = 0;
        dy = 0;
    }

    public void updatePos() {
        xpos += dx;
        ypos += dy;
    }

    public void tickStep() {
        dy += 0.2;
        updatePos();
    }

    public void draw(Graphics g) {
        g.drawOval((int) xpos, (int) ypos, 10, 10);
    }
}
