package enemy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ShooterEnemy extends Enemy {
    private static BufferedImage image;

    public ShooterEnemy(int homeRow, int homeCol, double baseX, double baseY, int hp) {
        super(homeRow, homeCol, baseX, baseY, hp, 25);
        speedMultiplier = 2.0;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/chicken/shooter_chicken.png"));
            } catch (IOException e) {
                System.out.println("Could not load ShooterEnemy image.");
            }
        }
    }

    @Override
    public String getTypeName() {
        return "Shooter";
    }

    @Override
    public Color baseColor() {
        return new Color(255, 120, 120);
    }

    @Override
    public void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            super.draw(g);
        }
    }
}