import java.awt.*;

public class Obstacle {
    private int x;
    private int y;
    private int width;
    private int height;
    private Color color;

    public Obstacle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isCircle() {
        return false;
    }

    public double getCenterX() {
        Rectangle r = getBounds();
        return r.getCenterX();
    }

    public double getCenterY() {
        Rectangle r = getBounds();
        return r.getCenterY();
    }

    public double getRadius() {
        Rectangle r = getBounds();
        return Math.min(r.width, r.height) / 2.0;
    }


    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}