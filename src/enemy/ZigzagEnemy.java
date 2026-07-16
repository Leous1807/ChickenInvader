package enemy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ZigzagEnemy extends Enemy {
    private double zigPhase = Math.random() * Math.PI * 2;
    private static BufferedImage image;

    public ZigzagEnemy(int homeRow, int homeCol, double baseX, double baseY, int hp) {
        super(homeRow, homeCol, baseX, baseY, hp, 20);
        speedMultiplier = 2.0;

        if (image == null) {
            try {
                image = ImageIO.read(new File("resources/chicken/zigzag_chicken.png"));
            } catch (IOException e) {
                System.out.println("Could not load ZigzagEnemy image.");
            }
        }
    }

    @Override
    public String getTypeName() {
        return "Zigzag";
    }

    @Override
    public Color baseColor() {
        return new Color(170, 120, 255);
    }

    @Override
    public boolean updateFlyIn() {
        zigPhase = zigPhase + 0.35;
        boolean arrived = super.updateFlyIn();
        if (arrived == false) {
            x = x + Math.sin(zigPhase) * 4;
        }
        return arrived;
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