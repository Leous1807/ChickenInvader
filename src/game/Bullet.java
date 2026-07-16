package game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {

    public double x, y;
    public double speed = 18.0;
    public int width = 24;
    public int height = 64;
    public boolean alive = true;
    public double bossDamageMultiplier = 1.0;

    private static BufferedImage image;

    public Bullet(double x, double y, double speed, double bossDamageMultiplier) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.bossDamageMultiplier = bossDamageMultiplier;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/airplan/shot.png"));
            } catch (IOException e) {
                System.out.println("Could not load Bullet image.");
            }
        }
    }

    public void update() {
        y = y - speed;
        if (y + height < 0) {
            alive = false;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRoundRect((int) x, (int) y, width, height, 4, 4);
            g.setColor(Color.WHITE);
            g.drawRoundRect((int) x, (int) y, width, height, 4, 4);
        }
    }
}