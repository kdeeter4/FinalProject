import java.awt.*;

public class Ball {
    // Movement info
    private double xpos;
    private double ypos;
    private double dx;
    private double dy;
    // Remember start position so we can reset the ball if it goes out of bounds
    private final double startX;
    private final double startY;
    private NoteBlockListener noteListener;
    // Magic Numbers
    public static final int SIZE = 35;
    private static final double GRAVITY = 1;
    private static final double BOUNCE = 0.9;

    private static final double STOP_SPEED = 1.0;
    // Constructor
    public Ball(double x, double y) {
        xpos = x;
        ypos = y;
        startX = x;
        startY = y;
        dx = 0;
        dy = 0;   // stationary until launch() is called
    }

    /** Snaps the ball back to its starting position, motionless. */
    public void reset() {
        xpos = startX;
        ypos = startY;
        dx = 0;
        dy = 0;
    }

    /** True if the ball has left the visible play area. */
    public boolean isOutOfBounds() {
        return xpos + SIZE < 0
                || xpos > GameView.WINDOW_WIDTH
                || ypos > GameView.WINDOW_HEIGHT
                || ypos + SIZE < 0;
    }

    /** Called when the player presses Play — gives the ball its initial kick. */
    public void launch() {
        dx = 0;
        dy = 0;
    }
    // Every tick where the ball moves, takes in level to move and see when bounce occurs
    public void tickStep(Level level) {
        // Gravity applies
        dy += GRAVITY;

        // find next x and y for good bounce collision code
        double nextX = xpos + dx;
        double nextY = ypos + dy;

        // No wall/floor/ceiling bouncing — ball passes through edges.
        // Game checks for out-of-bounds and resets the ball.
        xpos = nextX;
        ypos = nextY;

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