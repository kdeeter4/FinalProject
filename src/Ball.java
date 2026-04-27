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
        dx = 10;
        dy = -20;
    }
    // Every tick where the ball moves, takes in level to move and see when bounce occurs
    public void tickStep(Level level) {
        double prevX = xpos;
        double prevY = ypos;

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

        // Rectangle collisions: top, bottom, left, right
        if (level != null) {
            Rectangle ballPrev = new Rectangle((int) prevX, (int) prevY, SIZE, SIZE);
            Rectangle ballNow  = new Rectangle((int) xpos, (int) ypos, SIZE, SIZE);

            for (Obstacle obstacle : level.getObstacles()) {
                Rectangle r = obstacle.getBounds();

                if (!ballNow.intersects(r)) {
                    continue;
                }

                boolean cameFromAbove = ballPrev.y + ballPrev.height <= r.y;
                boolean cameFromBelow = ballPrev.y >= r.y + r.height;
                boolean cameFromLeft  = ballPrev.x + ballPrev.width <= r.x;
                boolean cameFromRight = ballPrev.x >= r.x + r.width;

                double overlapTop    = ballNow.y + ballNow.height - r.y;
                double overlapBottom = r.y + r.height - ballNow.y;
                double overlapLeft   = ballNow.x + ballNow.width - r.x;
                double overlapRight  = r.x + r.width - ballNow.x;

                // Pick the most likely collision face using movement direction first.
                if (cameFromAbove && dy >= 0 && overlapTop <= overlapLeft && overlapTop <= overlapRight) {
                    dy = -dy * BOUNCE;
                    ypos = r.y - SIZE;

                    if (Math.abs(dy) < STOP_SPEED) {
                        dy = 0;
                        ypos = r.y - SIZE;
                    }
                } else if (cameFromBelow && dy < 0 && overlapBottom <= overlapLeft && overlapBottom <= overlapRight) {
                    dy = -dy * BOUNCE;
                    ypos = r.y + r.height;
                } else if (cameFromLeft && dx > 0 && overlapLeft < overlapTop && overlapLeft < overlapBottom) {
                    dx = -dx * BOUNCE;
                    xpos = r.x - SIZE;
                } else if (cameFromRight && dx < 0 && overlapRight < overlapTop && overlapRight < overlapBottom) {
                    dx = -dx * BOUNCE;
                    xpos = r.x + r.width;
                } else {
                    // Fallback if corner overlap or high-speed penetration makes side unclear:
                    double minOverlap = Math.min(Math.min(overlapTop, overlapBottom),
                            Math.min(overlapLeft, overlapRight));

                    if (minOverlap == overlapTop) {
                        dy = -dy * BOUNCE;
                        ypos = r.y - SIZE;

                        if (Math.abs(dy) < STOP_SPEED) {
                            dy = 0;
                            ypos = r.y - SIZE;
                        }
                    } else if (minOverlap == overlapBottom) {
                        dy = -dy * BOUNCE;
                        ypos = r.y + r.height;
                    } else if (minOverlap == overlapLeft) {
                        dx = -dx * BOUNCE;
                        xpos = r.x - SIZE;
                    } else {
                        dx = -dx * BOUNCE;
                        xpos = r.x + r.width;
                    }
                }

                break;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) xpos, (int) ypos, SIZE, SIZE);
    }
}