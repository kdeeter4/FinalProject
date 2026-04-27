import java.awt.*;

public class Ball {
    // Movement info
    private double xpos;
    private double ypos;
    private double dx;
    private double dy;
    // Magic Numbers
    private static final int SIZE = 35;
    private static final double GRAVITY = 1;
    private static final double BOUNCE = 0.9;

    private static final double STOP_SPEED = 1.0;
    // Constructor
    public Ball(double x, double y) {
        // testing sample velocity
        xpos = x;
        ypos = y;
        dx = 0;
        dy = -20;
    }
    // Every tick where the ball moves, takes in level to move and see when bounce occurs
    public void tickStep(Level level) {
        dy += GRAVITY;

        double nextX = xpos + dx;
        double nextY = ypos + dy;

        // Left wall
        if (nextX < 0) {
            double overshoot = -nextX;
            xpos = overshoot;
            dx = -dx;
        } else if (nextX + SIZE > GameView.WINDOW_WIDTH) {
            double overshoot = nextX + SIZE - GameView.WINDOW_WIDTH;
            xpos = GameView.WINDOW_WIDTH - SIZE - overshoot;
            dx = -dx;
        } else {
            xpos = nextX;
        }

        // Ceiling
        if (nextY < 0) {
            double overshoot = -nextY;
            dy = -dy * BOUNCE;
            ypos = overshoot * BOUNCE;
        }
        // Floor
        else if (nextY + SIZE > GameView.WINDOW_HEIGHT) {
            double overshoot = nextY + SIZE - GameView.WINDOW_HEIGHT;
            dy = -dy * BOUNCE;
            ypos = GameView.WINDOW_HEIGHT - SIZE - overshoot * BOUNCE;

            if (Math.abs(dy) < STOP_SPEED) {
                dy = 0;
                ypos = GameView.WINDOW_HEIGHT - SIZE;
            }
        } else {
            ypos = nextY;
        }

        // Landing on top of obstacles
        if (level != null && dy >= 0) {
            Rectangle ballNow = new Rectangle((int) xpos, (int) (ypos - dy), SIZE, SIZE);
            Rectangle ballNext = new Rectangle((int) xpos, (int) ypos, SIZE, SIZE);

            for (Obstacle obstacle : level.getObstacles()) {
                Rectangle r = obstacle.getBounds();

                boolean wasAbove = ballNow.y + ballNow.height <= r.y;
                boolean crossedTop = ballNext.y + ballNext.height >= r.y;
                boolean overlapsX = ballNext.x + ballNext.width > r.x && ballNext.x < r.x + r.width;

                if (wasAbove && crossedTop && overlapsX) {
                    double bottomAfterMove = ypos + SIZE;
                    double overshoot = bottomAfterMove - r.y;

                    dy = -dy * BOUNCE;
                    ypos = r.y - SIZE - overshoot * BOUNCE;

                    if (Math.abs(dy) < STOP_SPEED) {
                        dy = 0;
                        ypos = r.y - SIZE;
                    }
                    break;
                }
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) xpos, (int) ypos, SIZE, SIZE);
    }
}