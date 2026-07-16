package enemy;

import entity.Egg;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Boss {

    public double x, y;
    public int width = 320, height = 260;
    public int hp, maxHp;
    public boolean alive = true;

    protected long lastAttackTime = 0;
    protected long attackIntervalMs;

    public Boss(int maxHp, long attackIntervalMs, double startX, double startY) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackIntervalMs = attackIntervalMs;
        this.x = startX;
        this.y = startY;
    }

    public abstract void updateMovement(int panelWidth);

    public abstract List<Egg> maybeAttack(long now);

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
        g.fillRoundRect((int) x, (int) y, width, height, 40, 40);
        g.setColor(Color.BLACK);
        g.drawRoundRect((int) x, (int) y, width, height, 40, 40);

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

    protected List<Egg> radialShot(int directions, double speed) {
        List<Egg> eggs = new ArrayList<>();
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        for (int i = 0; i < directions; i++) {
            double angle = (2 * Math.PI / directions) * i;
            double dx = Math.cos(angle) * speed;
            double dy = Math.sin(angle) * speed;
            eggs.add(new Egg(cx, cy, dx, dy));
        }
        return eggs;
    }
}