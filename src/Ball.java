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

    // Snaps the ball back to its starting position, motionless.
    public void reset() {
        xpos = startX;
        ypos = startY;
        dx = 0;
        dy = 0;
    }

    // True if the ball has left the visible play area.
    public boolean isOutOfBounds() {
        return xpos + SIZE < 0
                || xpos > GameView.WINDOW_WIDTH
                || ypos > GameView.WINDOW_HEIGHT
                || ypos + SIZE < 0;
    }


    /**
     * Advances the ball's physics by one game tick.
     * Handles gravity, movement, and collision with obstacles.
     *
     * @param level the current level containing obstacles to collide with
     */
    public void tickStep(Level level) {
        // --- GRAVITY ---
        // Accelerate the ball downward each tick (simulates gravity)
        dy += GRAVITY;

        // --- MOVEMENT ---
        // Calculate where the ball will be after this tick
        double nextX = xpos + dx;
        double nextY = ypos + dy;

        // No bouncing off walls, floors, or ceilings.
        // Out-of-bounds is handled elsewhere — the ball just moves freely.
        xpos = nextX;
        ypos = nextY;

        // --- OBSTACLE COLLISION ---
        if (level != null) {
            // Compute the ball's center point and radius for circular collision math
            double ballCenterX = xpos + SIZE / 2.0;
            double ballCenterY = ypos + SIZE / 2.0;
            double ballRadius = SIZE / 2.0;

            // Two bounding boxes: where the ball WAS (before this tick) and where it IS now.
            // Used to detect if the ball "crossed through" an obstacle's top edge.
            Rectangle ballNow  = new Rectangle((int) xpos, (int) (ypos - dy), SIZE, SIZE); // previous position
            Rectangle ballNext = new Rectangle((int) xpos, (int) ypos, SIZE, SIZE);         // current position

            for (Obstacle obstacle : level.getObstacles()) {
                // Use the NoteBlock's custom bounce value if applicable, otherwise use the default
                double bounce = obstacle instanceof NoteBlock
                        ? ((NoteBlock) obstacle).getBounce()
                        : BOUNCE;

                // ---- CIRCULAR OBSTACLE ----
                if (obstacle.isCircle()) {
                    double cx = obstacle.getCenterX(); // circle center X
                    double cy = obstacle.getCenterY(); // circle center Y
                    double r  = obstacle.getRadius();  // circle radius

                    // Vector from circle center to ball center
                    double nx   = ballCenterX - cx;
                    double ny   = ballCenterY - cy;
                    double dist = Math.sqrt(nx * nx + ny * ny); // actual distance between centers

                    // Check if the ball is overlapping the circle
                    if (dist <= ballRadius + r) {

                        // Normalize the collision normal vector (unit vector pointing away from circle)
                        if (dist == 0) {
                            // Exact overlap — default to pushing straight up to avoid divide-by-zero
                            nx = 0;
                            ny = -1;
                            dist = 1;
                        } else {
                            nx /= dist; // normalize X component
                            ny /= dist; // normalize Y component
                        }

                        // Reflect the velocity vector across the collision normal (standard reflection formula)
                        // dot = how much of the velocity is moving "into" the surface
                        double dot = dx * nx + dy * ny;
                        dx = (dx - 2 * dot * nx) * bounce; // reflected + dampened X velocity
                        dy = (dy - 2 * dot * ny) * bounce; // reflected + dampened Y velocity

                        // Push the ball out of the circle so they're just touching, not overlapping
                        double targetDist = ballRadius + r;
                        double pushOut    = targetDist - dist;
                        xpos += nx * pushOut;
                        ypos += ny * pushOut;

                        // If velocity is very small, stop the ball completely (prevents micro-jitter)
                        if (Math.abs(dx) < STOP_SPEED) dx = 0;
                        if (Math.abs(dy) < STOP_SPEED) dy = 0;

                        // Notify the note listener if this is a NoteBlock (triggers a sound/event)
                        if (obstacle instanceof NoteBlock && noteListener != null) {
                            noteListener.onNoteBlockHit((NoteBlock) obstacle);
                        }

                        break; // only collide with one obstacle per tick
                    }

                    // ---- RECTANGULAR OBSTACLE (only when falling downward) ----
                } else if (dy >= 0) {
                    Rectangle r = obstacle.getBounds();

                    // Check that the ball was ABOVE the obstacle last tick...
                    boolean wasAbove   = ballNow.y  + ballNow.height  <= r.y;
                    // ...and has now reached or crossed the obstacle's top edge...
                    boolean crossedTop = ballNext.y + ballNext.height >= r.y;
                    // ...and is horizontally aligned with the obstacle
                    boolean overlapsX  = ballNext.x + ballNext.width  >  r.x
                            && ballNext.x                   <  r.x + r.width;

                    // Only bounce if all three conditions are true (ball landed on top)
                    if (wasAbove && crossedTop && overlapsX) {
                        // How far the ball sank into the obstacle this tick
                        double bottomAfterMove = ypos + SIZE;
                        double overshoot       = bottomAfterMove - r.y;

                        // Reverse and dampen vertical velocity (bounce!)
                        dy = -dy * bounce;
                        // Reposition the ball just above the surface, offset by the bounce energy
                        ypos = r.y - SIZE - overshoot * bounce;

                        // If bouncing very weakly, just rest the ball on the surface
                        if (Math.abs(dy) < STOP_SPEED) {
                            dy   = 0;
                            ypos = r.y - SIZE;
                        }

                        // Notify the note listener if this is a NoteBlock
                        if (obstacle instanceof NoteBlock && noteListener != null) {
                            noteListener.onNoteBlockHit((NoteBlock) obstacle);
                        }

                        break; // only collide with one obstacle per tick
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


