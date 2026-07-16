package enemy;

import java.awt.*;

public abstract class Enemy {

    public double x, y;
    public int width = 80, height = 64;

    public int hp;
    public int maxHp;
    public int scoreValue;

    public int homeRow, homeCol;
    public double homeBaseX, homeBaseY;

    public boolean alive = true;
    public boolean flyingIn = false;
    private double targetX, targetY;
    private double flySpeed = 3.0;

    protected double speedMultiplier = 1.0;

    public Enemy(int homeRow, int homeCol, double homeBaseX, double homeBaseY, int hp, int scoreValue) {
        this.homeRow = homeRow;
        this.homeCol = homeCol;
        this.homeBaseX = homeBaseX;
        this.homeBaseY = homeBaseY;
        this.hp = hp;
        this.maxHp = hp;
        this.scoreValue = scoreValue;
    }

    public abstract String getTypeName();

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void startFlyIn(double fromX, double fromY, double toX, double toY) {
        this.x = fromX;
        this.y = fromY;
        this.targetX = toX;
        this.targetY = toY;
        this.flyingIn = true;
    }

    public boolean updateFlyIn() {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist < flySpeed) {
            x = targetX;
            y = targetY;
            flyingIn = false;
            return true;
        }
        x = x + flySpeed * dx / dist;
        y = y + flySpeed * dy / dist;
        return false;
    }

    public void setFormationPosition(double gridOffsetX, double gridOffsetY) {
        if (flyingIn == false) {
            this.x = homeBaseX + gridOffsetX;
            this.y = homeBaseY + gridOffsetY;
        }
    }

    public boolean hit(int damage) {
        hp = hp - damage;
        if (hp <= 0) {
            if (alive == true) {
                alive = false;
                return true;
            }
        }
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public abstract Color baseColor();

    public void draw(Graphics2D g) {
        g.setColor(baseColor());
        g.fillOval((int) x, (int) y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval((int) x, (int) y, width, height);
        if (maxHp > 1) {
            int pipW = 5;
            int gap = 2;
            int total = maxHp * pipW + (maxHp - 1) * gap;
            int sx = (int) x + (width - total) / 2;
            for (int i = 0; i < maxHp; i++) {
                if (i < hp) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(sx + i * (pipW + gap), (int) y - 8, pipW, 4);
            }
        }
    }
}