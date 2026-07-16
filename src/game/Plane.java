package game;

import entity.PowerUp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Plane {

    public double x, y;
    public int width = 96;
    public int height = 96;

    public int lives = 3;
    public int maxLives = 5;

    public double speed = 10.0;
    public long shotCooldownMs = 300;
    public double bossDamageMultiplier = 1.0;

    private long lastShotTime = 0;
    public int bulletsPerShot = 1;

    public boolean shieldActive = false;
    public long shieldEndTime = 0;

    public boolean rapidFireActive = false;
    public long rapidFireEndTime = 0;

    public String planeType = "Default";

    private Image planeImage;

    public Plane(String planeType) {
        applyPlaneType(planeType);
        loadImage();
    }

    private void loadImage() {
        try {
            String path = "resources/airplan/1.png";
            if (planeType.equals("Fast") == true) {
                path = "resources/airplan/5.png";
            } else if (planeType.equals("Heavy") == true) {
                path = "resources/airplan/3.png";
            } else if (planeType.equals("Sniper") == true) {
                path = "resources/airplan/6.png";
            }
            planeImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Could not load plane image! Check your file path.");
        }
    }

    private void applyPlaneType(String type) {
        this.planeType = type;
        if (type.equals("Fast") == true) {
            speed = 7.0;
            shotCooldownMs = 250;
            maxLives = 5;
            lives = 3;
            bossDamageMultiplier = 1.0;
        } else if (type.equals("Heavy") == true) {
            speed = 4.0;
            shotCooldownMs = 200;
            maxLives = 5;
            lives = 5;
            bossDamageMultiplier = 1.0;
        } else if (type.equals("Sniper") == true) {
            speed = 5.0;
            shotCooldownMs = 150;
            maxLives = 5;
            lives = 3;
            bossDamageMultiplier = 2.0;
        } else {
            speed = 5.0;
            shotCooldownMs = 300;
            maxLives = 5;
            lives = 3;
            bossDamageMultiplier = 1.0;
        }
    }

    public void move(double dx, double dy, int panelWidth, int panelHeight) {
        x = clamp(x + dx * speed, 0, panelWidth - width);
        y = clamp(y + dy * speed, 0, panelHeight - height);
    }

    private double clamp(double value, double min, double max) {
        double temporaryValue = value;
        if (temporaryValue < min) {
            temporaryValue = min;
        }
        if (temporaryValue > max) {
            temporaryValue = max;
        }
        return temporaryValue;
    }

    public boolean canShoot(long now) {
        long cooldown;
        if (rapidFireActive == true) {
            cooldown = shotCooldownMs / 3;
        } else {
            cooldown = shotCooldownMs;
        }
        if (now - lastShotTime >= cooldown) {
            return true;
        } else {
            return false;
        }
    }

    public List<Bullet> shoot(long now) {
        lastShotTime = now;
        List<Bullet> bullets = new ArrayList<>();
        int bulletCount = bulletsPerShot;
        double spacing = 14.0;
        double startX = x + width / 2.0 - (bulletCount - 1) * spacing / 2.0;
        for (int i = 0; i < bulletCount; i++) {
            bullets.add(new Bullet(startX + i * spacing - 3, y, 9.0, bossDamageMultiplier));
        }
        return bullets;
    }

    public boolean takeHit() {
        if (shieldActive == true) {
            return false;
        }
        lives = lives - 1;
        return true;
    }

    public void addLife() {
        int targetLives = lives + 1;
        if (targetLives > maxLives) {
            targetLives = maxLives;
        }
        lives = targetLives;
    }

    public void applyPowerUp(PowerUp.Type type, long now) {
        if (type == PowerUp.Type.ADD_FIRE) {
            bulletsPerShot = bulletsPerShot + 1;
        } else if (type == PowerUp.Type.RAPID_FIRE) {
            rapidFireActive = true;
            rapidFireEndTime = now + 8000;
        } else if (type == PowerUp.Type.EXTRA_LIFE) {
            addLife();
        } else if (type == PowerUp.Type.SHIELD) {
            shieldActive = true;
            shieldEndTime = now + 10000;
        }
    }

    public void update(long now) {
        if (rapidFireActive == true) {
            if (now >= rapidFireEndTime) {
                rapidFireActive = false;
            }
        }
        if (shieldActive == true) {
            if (now >= shieldEndTime) {
                shieldActive = false;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x + 24, (int) y + 24, width - 48, height - 48);
    }

    public void draw(Graphics2D g) {
        if (planeImage != null) {
            g.drawImage(planeImage, (int) x, (int) y, width, height, null);
        } else {
            g.setColor(new Color(70, 160, 230));
            int[] xPoints = new int[3];
            xPoints[0] = (int) x + width / 2;
            xPoints[1] = (int) x;
            xPoints[2] = (int) x + width;

            int[] yPoints = new int[3];
            yPoints[0] = (int) y;
            yPoints[1] = (int) y + height;
            yPoints[2] = (int) y + height;

            g.fillPolygon(xPoints, yPoints, 3);
            g.setColor(Color.WHITE);
            g.fillRect((int) x + width / 2 - 4, (int) y + height - 14, 8, 14);
        }

        if (shieldActive == true) {
            g.setColor(new Color(120, 255, 150, 120));
            g.fillOval((int) x - 8, (int) y - 8, width + 16, height + 16);
            g.setColor(new Color(120, 255, 150));
            g.drawOval((int) x - 8, (int) y - 8, width + 16, height + 16);
        }
    }
}