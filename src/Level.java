import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {

    private final int levelNumber;
    private final Color backgroundColor;
    private final Rectangle target;
    private final Tune targetTune;
    private final int ballSpawnX;
    private final int ballSpawnY;

    private final List<Obstacle> fixedObstacles;
    private final List<Obstacle> placedBlocks;
    private final List<Note> paletteNotes;

    public Level(int levelNumber, Color backgroundColor, Rectangle target, Tune targetTune,
                 int ballSpawnX, int ballSpawnY, List paletteNotes) {
        this.levelNumber = levelNumber; // Level 1, 2, etc
        this.backgroundColor = backgroundColor; // background color
        this.target = target; // Green rectangle target
        this.targetTune = targetTune; // Tune user wants to make from bounces
        this.ballSpawnX = ballSpawnX; // Ball spawn coordinates
        this.ballSpawnY = ballSpawnY;
        this.paletteNotes = new ArrayList<>(paletteNotes); // Notes available in palette
        this.fixedObstacles = new ArrayList<>(); // Rectangle bouncing obstacles
        this.placedBlocks = new ArrayList<>();  // Placed noteblocks. edited in play
    }

    // Factory for Level 1, using your layout + target tune
    public static Level makeLevel1() {
        Tune.NoteEvent[] target = {
                new Tune.NoteEvent(new Note("C4"), 400, 100),
                new Tune.NoteEvent(new Note("E4"), 400, 100),
                new Tune.NoteEvent(new Note("G4"), 400, 100)
        };

        Level level = new Level(
                1,
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
    // Factory for Level 2 — target tune: C4 D4 E4 F4 G4 (rising scale, 5 notes)
    public static Level makeLevel2() {
        Tune.NoteEvent[] target = {
                new Tune.NoteEvent(new Note("C4"), 350, 80),
                new Tune.NoteEvent(new Note("D4"), 350, 80),
                new Tune.NoteEvent(new Note("E4"), 350, 80),
                new Tune.NoteEvent(new Note("F4"), 350, 80),
                new Tune.NoteEvent(new Note("G4"), 350, 80)
        };

        Level level = new Level(
                2,
                new Color(230, 245, 255),            // light blue background
                new Rectangle(550, 650, 160, 120),   // goal area
                new Tune(target),
                150,   // ballSpawnX
                80,    // ballSpawnY
                Arrays.asList(
                        new Note("C4"), new Note("D4"), new Note("E4"),
                        new Note("F4"), new Note("G4"), new Note("A4"),
                        new Note("B4"), new Note("C5")
                )
        );

        // Zigzag platforms
        level.addFixedObstacle(new Obstacle(100, 580, 150, 20, new Color(60, 60, 180)));
        level.addFixedObstacle(new Obstacle(320, 460, 150, 20, new Color(60, 60, 180)));
        level.addFixedObstacle(new Obstacle(130, 340, 150, 20, new Color(60, 60, 180)));
        level.addFixedObstacle(new Obstacle(400, 230, 150, 20, new Color(60, 60, 180)));

        return level;
    }

    // Factory for Level 3 — target tune: E4 G4 B4 E5 (arpeggio, 4 notes)
    public static Level makeLevel3() {
        Tune.NoteEvent[] target = {
                new Tune.NoteEvent(new Note("E4"), 300, 100),
                new Tune.NoteEvent(new Note("G4"), 300, 100),
                new Tune.NoteEvent(new Note("B4"), 300, 100),
                new Tune.NoteEvent(new Note("E5"), 300, 100)
        };

        Level level = new Level(
                3,
                new Color(255, 240, 230),            // warm orange background
                new Rectangle(480, 620, 180, 140),   // goal area
                new Tune(target),
                300,   // ballSpawnX — starts in middle
                60,    // ballSpawnY
                Arrays.asList(
                        new Note("C4"), new Note("D4"), new Note("E4"),
                        new Note("F4"), new Note("G4"),
                        new Note("B4"), new Note("E5")
                )
        );

        // Staircase-style platforms going right-to-left
        level.addFixedObstacle(new Obstacle(500, 560, 140, 20, new Color(180, 80, 30)));
        level.addFixedObstacle(new Obstacle(280, 440, 140, 20, new Color(180, 80, 30)));
        level.addFixedObstacle(new Obstacle(460, 310, 140, 20, new Color(180, 80, 30)));
        level.addFixedObstacle(new Obstacle(200, 200, 140, 20, new Color(180, 80, 30)));

        return level;
    }

    public int getLevelNumber() {
        return levelNumber;
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


