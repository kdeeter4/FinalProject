import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {

    private final Color backgroundColor;
    private final Rectangle target;
    private final Tune targetTune;
    private final int ballSpawnX;
    private final int ballSpawnY;

    private final List<Obstacle> fixedObstacles;
    private final List<Obstacle> placedBlocks;
    private final List<Note> paletteNotes;

    public Level(Color backgroundColor, Rectangle target, Tune targetTune,
                 int ballSpawnX, int ballSpawnY, List<Note> paletteNotes) {
        this.backgroundColor = backgroundColor;
        this.target = target;
        this.targetTune = targetTune;
        this.ballSpawnX = ballSpawnX;
        this.ballSpawnY = ballSpawnY;
        this.paletteNotes = new ArrayList<>(paletteNotes);
        this.fixedObstacles = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
    }

    // Factory for Level 1, using your layout + target tune
    public static Level makeLevel1() {
        Tune.NoteEvent[] target = {
                new Tune.NoteEvent(new Note("C4"), 400, 100),
                new Tune.NoteEvent(new Note("E4"), 400, 100),
                new Tune.NoteEvent(new Note("G4"), 400, 100)
        };

        Level level = new Level(
                Color.WHITE,
                new Rectangle(600, 680, 200, 120),   // goal: bottom-right, fully visible
                new Tune(target),
                200,   // ballSpawnX
                100,   // ballSpawnY — below the HUD
                Arrays.asList(
                        new Note("C4"), new Note("D4"), new Note("E4"),
                        new Note("F4"), new Note("G4"), new Note("A4"), new Note("B4")
                )
        );

        // Three staggered platforms leading toward goal
        level.addFixedObstacle(new Obstacle(150, 560, 170, 20, Color.BLACK));
        level.addFixedObstacle(new Obstacle(370, 430, 160, 20, Color.BLACK));
        level.addFixedObstacle(new Obstacle(530, 310, 130, 20, Color.BLACK));

        return level;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Rectangle getTarget() {
        return target;
    }

    public Tune getTargetTune() {
        return targetTune;
    }

    public int getBallSpawnX() {
        return ballSpawnX;
    }

    public int getBallSpawnY() {
        return ballSpawnY;
    }

    public void addFixedObstacle(Obstacle obstacle) {
        fixedObstacles.add(obstacle);
    }

    public void addPlacedNoteBlock(NoteBlock block) {
        placedBlocks.add(block);
    }

    public void removePlacedBlock(int index) {
        placedBlocks.remove(index);
    }

    public void clearPlacedBlocks() {
        placedBlocks.clear();
    }

    public void resetPlacedBlocks() {
        clearPlacedBlocks();
    }

    public List<Obstacle> getFixedObstacles() {
        return new ArrayList<>(fixedObstacles);
    }

    public List<Obstacle> getPlacedBlocks() {
        return new ArrayList<>(placedBlocks);
    }

    public List<Obstacle> getAllObstacles() {
        List<Obstacle> all = new ArrayList<>(fixedObstacles);
        all.addAll(placedBlocks);
        return all;
    }

    public List<Note> getPaletteNotes() {
        return new ArrayList<>(paletteNotes);
    }

    // Keep this so Ball.tickStep(...) does not need to change.
    public List<Obstacle> getObstacles() {
        return getAllObstacles();
    }

    // Alias so main's Game.java doesn't need changes
    public void addObstacle(Obstacle obstacle) {
        addFixedObstacle(obstacle);
    }

    public List<NoteBlock> makePalette() {
        List<NoteBlock> palette = new ArrayList<>();
        for (int i = 0; i < paletteNotes.size(); i++) {
            palette.add(new NoteBlock(paletteNotes.get(i), 400, 20, 100 + i * 70));
        }
        return palette;
    }

    public void draw(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, GameView.WINDOW_WIDTH, GameView.WINDOW_HEIGHT);

        g.setColor(Color.GREEN);
        g.fillRect(target.x, target.y, target.width, target.height);

        for (Obstacle obstacle : getAllObstacles()) {
            obstacle.draw(g);
        }
    }
}