package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Egg {

    public double x, y;
    public double dx, dy;
    public int width = 56;
    public int height = 76;
    public boolean alive = true;
    public boolean frozen = false;

    private static BufferedImage image;

    public Egg(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/chicken/egg.png"));
            } catch (IOException e) {
                System.out.println("Could not load Egg image.");
            }
        }
    }

    public void update(int panelWidth, int panelHeight) {
        if (frozen == true) {
            return;
        }
        x = x + dx;
        y = y + dy;
        if (x < -width || x > panelWidth + width || y < -height || y > panelHeight + height) {
            alive = false;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x + 12, (int) y + 16, width - 24, height - 32);
    }

    public void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(255, 245, 210));
            g.fillOval((int) x, (int) y, width, height);
            g.setColor(Color.ORANGE.darker());
            g.drawOval((int) x, (int) y, width, height);
        }
    }
}