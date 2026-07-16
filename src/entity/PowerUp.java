package entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PowerUp {

    public enum Type { RAPID_FIRE, FREEZE_BOMB, EXTRA_LIFE, SHIELD, ADD_FIRE }

    public double x, y;
    public double speed = 4.0;
    public Type type;
    public boolean alive = true;
    public int width = 52;
    public int height = 52;

    private static BufferedImage imgRapid, imgFreeze, imgExtra, imgShield, imgAddFire;
    private static boolean imagesLoaded = false;

    public PowerUp(double x, double y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;

        if (imagesLoaded == false) {
            loadImages();
            imagesLoaded = true;
        }
    }

    private void loadImages() {
        try {
            imgRapid = ImageIO.read(new File("resources/powerup1/fast_shot.png"));
            imgFreeze = ImageIO.read(new File("resources/powerup1/freeze.png"));
            imgExtra = ImageIO.read(new File("resources/powerup1/heal.png"));
            imgShield = ImageIO.read(new File("resources/powerup1/sheild.png"));
            imgAddFire = ImageIO.read(new File("resources/powerup1/add_shot.png"));
        } catch (IOException e) {
            System.out.println("Could not load one or more PowerUp images.");
        }
    }

    public void update(int panelHeight) {
        y = y + speed;
        if (y > panelHeight) {
            alive = false;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    private Color colorFor(Type t) {
        if (t == Type.RAPID_FIRE) {
            return new Color(255, 200, 0);
        } else if (t == Type.FREEZE_BOMB) {
            return new Color(120, 220, 255);
        } else if (t == Type.EXTRA_LIFE) {
            return new Color(255, 80, 80);
        } else if (t == Type.SHIELD) {
            return new Color(120, 255, 150);
        } else if (t == Type.ADD_FIRE) {
            return new Color(255, 140, 0);
        } else {
            return Color.WHITE;
        }
    }

    private String letterFor(Type t) {
        if (t == Type.RAPID_FIRE) {
            return "R";
        } else if (t == Type.FREEZE_BOMB) {
            return "F";
        } else if (t == Type.EXTRA_LIFE) {
            return "+";
        } else if (t == Type.SHIELD) {
            return "S";
        } else if (t == Type.ADD_FIRE) {
            return "A";
        } else {
            return "?";
        }
    }

    public void draw(Graphics2D g) {
        BufferedImage activeImage = null;
        if (type == Type.RAPID_FIRE) {
            activeImage = imgRapid;
        } else if (type == Type.FREEZE_BOMB) {
            activeImage = imgFreeze;
        } else if (type == Type.EXTRA_LIFE) {
            activeImage = imgExtra;
        } else if (type == Type.SHIELD) {
            activeImage = imgShield;
        } else if (type == Type.ADD_FIRE) {
            activeImage = imgAddFire;
        }

        if (activeImage != null) {
            g.drawImage(activeImage, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(colorFor(type));
            g.fillOval((int) x, (int) y, width, height);
            g.setColor(Color.BLACK);
            g.drawOval((int) x, (int) y, width, height);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            FontMetrics fontMetrics = g.getFontMetrics();
            String stringToDraw = letterFor(type);
            int textX = (int) x + (width - fontMetrics.stringWidth(stringToDraw)) / 2;
            int textY = (int) y + (height + fontMetrics.getAscent()) / 2 - 2;
            g.drawString(stringToDraw, textX, textY);
        }
    }
}