import java.awt.*;

public class Ball {
    // Movement info
    private double xpos;
    private double ypos;
    private double dx;
    private double dy;
    private NoteBlockListener noteListener;
    // Magic Numbers
    public static final int SIZE = 35;
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
        // Gravity applies
        dy += GRAVITY;

        // find next x and y for good bounce collision code
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
            // If no collisions, just move ball normally
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
            // If no collisions, just move ball normally
            ypos = nextY;
        }

        // Collision with obstacles
        if (level != null) {
            double ballCenterX = xpos + SIZE / 2.0;
            double ballCenterY = ypos + SIZE / 2.0;
            double ballRadius = SIZE / 2.0;

            Rectangle ballNow = new Rectangle((int) xpos, (int) (ypos - dy), SIZE, SIZE);
            Rectangle ballNext = new Rectangle((int) xpos, (int) ypos, SIZE, SIZE);

            for (Obstacle obstacle : level.getObstacles()) {
                double bounce = obstacle instanceof NoteBlock
                        ? ((NoteBlock) obstacle).getBounce()
                        : BOUNCE;

                if (obstacle.isCircle()) {
                    double cx = obstacle.getCenterX();
                    double cy = obstacle.getCenterY();
                    double r = obstacle.getRadius();

                    double nx = ballCenterX - cx;
                    double ny = ballCenterY - cy;
                    double dist = Math.sqrt(nx * nx + ny * ny);

                    if (dist <= ballRadius + r) {
                        if (dist == 0) {
                            nx = 0;
                            ny = -1;
                            dist = 1;
                        } else {
                            nx /= dist;
                            ny /= dist;
                        }

                        double dot = dx * nx + dy * ny;
                        dx = (dx - 2 * dot * nx) * bounce;
                        dy = (dy - 2 * dot * ny) * bounce;

                        double targetDist = ballRadius + r;
                        double pushOut = targetDist - dist;
                        xpos += nx * pushOut;
                        ypos += ny * pushOut;

                        if (Math.abs(dx) < STOP_SPEED) dx = 0;
                        if (Math.abs(dy) < STOP_SPEED) dy = 0;

                        if (obstacle instanceof NoteBlock && noteListener != null) {
                            noteListener.onNoteBlockHit((NoteBlock) obstacle);
                        }

                        break;
                    }
                } else if (dy >= 0) {
                    Rectangle r = obstacle.getBounds();

                    boolean wasAbove = ballNow.y + ballNow.height <= r.y;
                    boolean crossedTop = ballNext.y + ballNext.height >= r.y;
                    boolean overlapsX = ballNext.x + ballNext.width > r.x && ballNext.x < r.x + r.width;

                    if (wasAbove && crossedTop && overlapsX) {
                        double bottomAfterMove = ypos + SIZE;
                        double overshoot = bottomAfterMove - r.y;

                        dy = -dy * bounce;
                        ypos = r.y - SIZE - overshoot * bounce;

                        if (Math.abs(dy) < STOP_SPEED) {
                            dy = 0;
                            ypos = r.y - SIZE;
                        }

                        if (obstacle instanceof NoteBlock && noteListener != null) {
                            noteListener.onNoteBlockHit((NoteBlock) obstacle);
                        }

                        break;
                    }
                }
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) xpos, (int) ypos, SIZE, SIZE);
    }
    // set the note block listener, which records when you hit a noteblock and what note it is for grading
    public void setNoteBlockListener(NoteBlockListener l) {
        this.noteListener = l;
    }

    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }
}