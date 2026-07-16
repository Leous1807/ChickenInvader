package enemy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FastEnemy extends Enemy {
    private static BufferedImage image;

    public FastEnemy(int homeRow, int homeCol, double baseX, double baseY, int hp) {
        super(homeRow, homeCol, baseX, baseY, hp, 15);
        speedMultiplier = 4.0;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/chicken/fast_chicken.png"));
            } catch (IOException e) {
                System.out.println("Could not load FastEnemy image.");
            }
        }
    }

    @Override
    public String getTypeName() {
        return "Fast";
    }

    @Override
    public Color baseColor() {
        return new Color(255, 210, 90);
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