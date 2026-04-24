import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private Color backgroundColor;
    private List<Obstacle> obstacles;
    private Rectangle target;

    public Level(Color backgroundColor, Rectangle target) {
        this.backgroundColor = backgroundColor;
        this.target = target;
        this.obstacles = new ArrayList<>();
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public Rectangle getTarget() {
        return target;
    }

    public void draw(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, GameView.WINDOW_WIDTH, GameView.WINDOW_HEIGHT);

        g.setColor(Color.GREEN);
        g.fillRect(target.x, target.y, target.width, target.height);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g);
        }
    }
}