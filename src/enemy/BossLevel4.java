package enemy;

import entity.Egg;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BossLevel4 extends Boss {

    private double hSpeed = 3;
    private double vPhase = 0;
    private final double baseY;
    private BufferedImage image;

    public BossLevel4(double startX, double startY) {
        super(100, 1500, startX, startY);
        this.baseY = startY;
        try {
            image = ImageIO.read(new File("resources/chicken/boss1.png"));
        } catch (IOException e) {
            System.out.println("Could not load BossLevel4 image. Falling back to default color.");
            image = null;
        }
    }

    @Override
    public void updateMovement(int panelWidth) {
        x = x + hSpeed;
        if (x <= 0 || x + width >= panelWidth) {
            hSpeed = -hSpeed;
        }
        vPhase = vPhase + 0.03;
        y = baseY + Math.sin(vPhase) * 12;
    }

    @Override
    public List<Egg> maybeAttack(long now) {
        if (now - lastAttackTime >= attackIntervalMs) {
            lastAttackTime = now;
            return radialShot(4, 4.0);
        }
        List<Egg> emptyList = new ArrayList<>();
        return emptyList;
    }

    @Override
    public Color baseColor() {
        return new Color(200, 90, 90);
    }

    @Override
    public void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            super.draw(g);
            return;
        }

        int barWidth = 300;
        int barHeight = 18;
        int bx = (int) x + width / 2 - barWidth / 2;
        int by = (int) y - 30;
        double ratio = hp / (double) maxHp;
        if (ratio < 0.0) {
            ratio = 0.0;
        }

        g.setColor(Color.DARK_GRAY);
        g.fillRect(bx, by, barWidth, barHeight);

        Color fillColor;
        if (ratio > 0.5) {
            fillColor = Color.GREEN;
        } else if (ratio > 0.2) {
            fillColor = Color.ORANGE;
        } else {
            fillColor = Color.RED;
        }
        g.setColor(fillColor);
        g.fillRect(bx, by, (int) (barWidth * ratio), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(bx, by, barWidth, barHeight);
        g.drawString(hp + " / " + maxHp, bx + barWidth / 2 - 20, by + 14);
    }
}