package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Explosion {

    public double x, y;
    private int age = 0;
    private final int lifeFrames;
    public boolean alive = true;
    private final int maxRadius;

    private static BufferedImage image;

    public Explosion(double x, double y) {
        this(x, y, 18, 34);
    }

    public Explosion(double x, double y, int lifeFrames, int maxRadius) {
        this.x = x;
        this.y = y;
        this.lifeFrames = lifeFrames;
        this.maxRadius = maxRadius * 2;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/airplan/Explosion.png"));
            } catch (IOException e) {
                System.out.println("Could not load Explosion image.");
            }
        }
    }

    public void update() {
        age = age + 1;
        if (age >= lifeFrames) {
            alive = false;
        }
    }

    public void draw(Graphics2D g) {
        float progress = age / (float) lifeFrames;
        int radius = (int) (maxRadius * progress);
        float alpha = 1.0f - progress;
        if (alpha < 0.0f) {
            alpha = 0.0f;
        }

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        if (image != null) {
            g.drawImage(image, (int) (x - radius / 2.0), (int) (y - radius / 2.0), radius, radius, null);
        } else {
            g.setColor(new Color(255, 200, 60));
            g.fillOval((int) (x - radius / 2.0), (int) (y - radius / 2.0), radius, radius);
            g.setColor(new Color(255, 90, 20));
            int inner = radius - 8;
            if (inner < 0) {
                inner = 0;
            }
            g.fillOval((int) (x - inner / 2.0), (int) (y - inner / 2.0), inner, inner);
        }

        g.setComposite(oldComposite);
    }
}