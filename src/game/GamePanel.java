package game;

import audio.SoundManager;
import data.DatabaseManager;
import data.User;
import enemy.*;
import entity.Egg;
import entity.Explosion;
import entity.PowerUp;
import main.GameMain;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int FPS_DELAY_MS = 16;

    private final GameMain gameMain;
    private final DatabaseManager db;
    private final SoundManager sound;
    private final User currentUser;

    private final Timer timer;
    private final boolean[] held = new boolean[600];

    private Plane plane;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Cell> cells = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Egg> eggs = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private Boss boss;

    private int level = 1;
    private int score = 0;
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean victory = false;
    private boolean recordSaved = false;
    private boolean showSettingsOverlay = false;

    private double gridOffsetX = 0, gridOffsetY = 0;
    private double gridDirection = 1;
    private double gridSpeed = 1.0;
    private double gridEdgeStep = 20;
    private long eggIntervalMs = 3000;
    private long lastEggDropTime = 0;

    private boolean freezeActive = false;
    private long freezeEndTime = 0;

    private final Random rng = new Random();
    private BufferedImage backgroundImg;

    public GamePanel(GameMain gameMain, DatabaseManager db, SoundManager sound, User currentUser) {
        this.gameMain = gameMain;
        this.db = db;
        this.sound = sound;
        this.currentUser = currentUser;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        String planeType = "Default";
        if (currentUser != null) {
            planeType = currentUser.getSelectedPlane();
        }
        plane = new Plane(planeType);
        plane.x = WIDTH / 2.0 - plane.width / 2.0;
        plane.y = HEIGHT - 80;

        loadBackground();
        sound.playMusicLoop("Chicken Invaders 2 Remastered OST - Main Theme.wav");

        startLevel(1);

        timer = new Timer(FPS_DELAY_MS, this);
        timer.start();
    }

    private void loadBackground() {
        File f = new File("resources/background/background.jpg");
        if (f.exists()) {
            try {
                backgroundImg = javax.imageio.ImageIO.read(f);
            } catch (Exception e) {
                backgroundImg = null;
            }
        }
    }

    private void startLevel(int newLevel) {
        this.level = newLevel;

        bullets.clear();
        eggs.clear();
        powerUps.clear();
        explosions.clear();
        enemies.clear();
        cells.clear();
        boss = null;
        gridOffsetX = 0;
        gridOffsetY = 0;
        gridDirection = 1;
        lastEggDropTime = System.currentTimeMillis();

        if (newLevel == 4) {
            boss = new BossLevel4(WIDTH / 2.0 - 80, 60);
        } else if (newLevel == 8) {
            boss = new BossLevel8(WIDTH / 2.0 - 80, 60);
        } else {
            buildGrid(newLevel);
        }
    }

    private void buildGrid(int lvl) {
        int rows = 5;
        int cols = 8;
        double cellW = 80;
        double cellH = 55;
        double startX = 40;
        double startY = 50;

        if (lvl == 1) {
            gridSpeed = 1.0;
            gridEdgeStep = 20;
            eggIntervalMs = 3000;
        } else if (lvl == 2) {
            gridSpeed = 1.5;
            gridEdgeStep = 20;
            eggIntervalMs = 2000;
        } else if (lvl == 3) {
            gridSpeed = 2.0;
            gridEdgeStep = 25;
            eggIntervalMs = 1500;
        } else if (lvl == 5) {
            gridSpeed = 2.5;
            gridEdgeStep = 25;
            eggIntervalMs = 1000;
        } else if (lvl == 6) {
            gridSpeed = 3.0;
            gridEdgeStep = 30;
            eggIntervalMs = 800;
        } else if (lvl == 7) {
            gridSpeed = 3.5;
            gridEdgeStep = 30;
            eggIntervalMs = 700;
        } else {
            gridSpeed = 1.0;
            gridEdgeStep = 20;
            eggIntervalMs = 3000;
        }

        int counter;
        if (lvl == 1 || lvl == 2) {
            counter = 2;
        } else if (lvl == 3 || lvl == 5) {
            counter = 3;
        } else if (lvl == 6 || lvl == 7) {
            counter = 4;
        } else {
            counter = 2;
        }

        String[] typePool = typesForLevel(lvl);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double bx = startX + c * cellW;
                double by = startY + r * cellH;
                String type = typePool[rng.nextInt(typePool.length)];
                Cell cell = new Cell(r, c, bx, by, type, counter);
                cells.add(cell);
                Enemy e = createEnemy(type, cell, lvl);
                e.setFormationPosition(0, 0);
                cell.occupant = e;
                enemies.add(e);
            }
        }
    }

    private String[] typesForLevel(int lvl) {
        if (lvl == 1) {
            return new String[]{"Normal"};
        } else if (lvl == 2) {
            return new String[]{"Normal", "Fast"};
        } else if (lvl == 3) {
            return new String[]{"Normal", "Zigzag"};
        } else if (lvl == 5) {
            return new String[]{"Shooter", "Fast"};
        } else if (lvl == 6) {
            return new String[]{"Zigzag", "Shooter"};
        } else if (lvl == 7) {
            return new String[]{"Normal", "Fast", "Zigzag", "Shooter"};
        } else {
            return new String[]{"Normal"};
        }
    }

    private int hpFor(String type, int lvl) {
        boolean strong = (lvl >= 5);

        if ("Normal".equals(type) || "Zigzag".equals(type) || "Shooter".equals(type)) {
            return strong ? 3 : 2;
        } else if ("Fast".equals(type)) {
            return strong ? 2 : 1;
        } else {
            return 2;
        }
    }

    private Enemy createEnemy(String type, Cell cell, int lvl) {
        int hp = hpFor(type, lvl);
        if ("Fast".equals(type)) {
            return new FastEnemy(cell.row, cell.col, cell.baseX, cell.baseY, hp);
        } else if ("Zigzag".equals(type)) {
            return new ZigzagEnemy(cell.row, cell.col, cell.baseX, cell.baseY, hp);
        } else if ("Shooter".equals(type)) {
            return new ShooterEnemy(cell.row, cell.col, cell.baseX, cell.baseY, hp);
        } else {
            return new NormalEnemy(cell.row, cell.col, cell.baseX, cell.baseY, hp);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.currentTimeMillis();
        if (!paused && !gameOver && !victory) {
            handleContinuousInput(now);
            updateFreezeState(now);
            updatePlaneAndBullets(now);

            if (boss != null) {
                updateBoss(now);
            } else {
                updateGrid(now);
            }

            updateEggs();
            updatePowerUps();
            updateExplosions();
            checkLevelProgress();
        }
        repaint();
    }

    private void handleContinuousInput(long now) {
        double dx = 0;
        double dy = 0;
        if (held[KeyEvent.VK_LEFT] || held[KeyEvent.VK_A]) {
            dx -= 1;
        }
        if (held[KeyEvent.VK_RIGHT] || held[KeyEvent.VK_D]) {
            dx += 1;
        }
        if (held[KeyEvent.VK_UP] || held[KeyEvent.VK_W]) {
            dy -= 1;
        }
        if (held[KeyEvent.VK_DOWN] || held[KeyEvent.VK_S]) {
            dy += 1;
        }

        if (dx != 0 || dy != 0) {
            plane.move(dx, dy, WIDTH, HEIGHT);
        }

        if (held[KeyEvent.VK_SPACE]) {
            if (plane.canShoot(now)) {
                bullets.addAll(plane.shoot(now));
                sound.playShot();
            }
        }
    }

    private void updateFreezeState(long now) {
        if (freezeActive && now >= freezeEndTime) {
            freezeActive = false;
            for (Egg egg : eggs) {
                egg.frozen = false;
            }
        }
    }

    private void updatePlaneAndBullets(long now) {
        plane.update(now);

        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update();
            if (!b.alive) {
                bit.remove();
                continue;
            }

            if (boss != null) {
                if (b.getBounds().intersects(boss.getBounds())) {
                    boolean died = boss.hit((int) Math.round(b.bossDamageMultiplier));
                    bit.remove();
                    explosions.add(new Explosion(b.x, b.y));

                    if (died) {
                        onBossDefeated();
                        break;
                    }
                }
            } else {
                Enemy hitEnemy = null;
                for (Enemy en : enemies) {
                    if (en.alive && b.getBounds().intersects(en.getBounds())) {
                        hitEnemy = en;
                        break;
                    }
                }
                if (hitEnemy != null) {
                    bit.remove();
                    boolean died = hitEnemy.hit(1);
                    if (died) {
                        onEnemyDefeated(hitEnemy);
                    }
                }
            }
        }
    }

    private void onEnemyDefeated(Enemy enemy) {
        explosions.add(new Explosion(enemy.x + enemy.width / 2.0, enemy.y + enemy.height / 2.0));
        sound.playCrash();
        score += enemy.scoreValue;
        enemies.remove(enemy);

        Cell cell = findCell(enemy.homeRow, enemy.homeCol);
        if (cell != null) {
            cell.occupant = null;
            cell.counterRemaining--;
            if (cell.counterRemaining > 0) {
                Enemy replacement = createEnemy(enemy.getTypeName(), cell, level);
                boolean fromLeft = rng.nextBoolean();
                double fromX = fromLeft ? -replacement.width : WIDTH;
                replacement.startFlyIn(fromX, -replacement.height, cell.baseX + gridOffsetX, cell.baseY + gridOffsetY);
                cell.occupant = replacement;
                enemies.add(replacement);
            }
        }

        if (rng.nextInt(100) < 10) {
            spawnPowerUp(enemy.x + enemy.width / 2.0, enemy.y + enemy.height / 2.0);
        }
    }

    private void onBossDefeated() {
        explosions.add(new Explosion(boss.x + boss.width / 2.0, boss.y + boss.height / 2.0, 30, 90));
        sound.playCrash();
        score += (level == 4) ? 500 : 1000;

        boss = null;

        if (level == 4) {
            startLevel(5);
        } else {
            triggerVictory();
        }
    }

    private Cell findCell(int row, int col) {
        for (Cell c : cells) {
            if (c.row == row && c.col == col) {
                return c;
            }
        }
        return null;
    }

    private void spawnPowerUp(double x, double y) {
        PowerUp.Type[] types = PowerUp.Type.values();
        PowerUp.Type t = types[rng.nextInt(types.length)];
        powerUps.add(new PowerUp(x, y, t));
    }

    private void updateGrid(long now) {
        if (!freezeActive) {
            gridOffsetX += gridSpeed * gridDirection;

            double leftMost = Double.MAX_VALUE;
            double rightMost = -Double.MAX_VALUE;
            boolean any = false;

            for (Cell c : cells) {
                if (c.occupant != null && c.occupant.alive && !c.occupant.flyingIn) {
                    any = true;
                    double leftVal = c.baseX + gridOffsetX;
                    double rightVal = c.baseX + gridOffsetX + c.occupant.width;
                    if (leftVal < leftMost) {
                        leftMost = leftVal;
                    }
                    if (rightVal > rightMost) {
                        rightMost = rightVal;
                    }
                }
            }

            if (any) {
                if (leftMost <= 0 || rightMost >= WIDTH) {
                    gridDirection = -gridDirection;
                    gridOffsetX += gridSpeed * gridDirection * 2;
                    gridOffsetY += gridEdgeStep;
                }
            }

            Iterator<Enemy> it = enemies.iterator();
            while (it.hasNext()) {
                Enemy en = it.next();

                if (en.y > HEIGHT) {
                    en.homeBaseY = -en.height - gridOffsetY;
                }

                if (!en.alive) {
                    it.remove();
                    continue;
                }

                if (en.flyingIn) {
                    en.flyingIn = false;
                    en.updateFlyIn();
                } else {
                    en.setFormationPosition(gridOffsetX, gridOffsetY);
                }
            }
            maybeDropEgg(now);
            maybeShooterFire(now);
        }
    }

    private void maybeDropEgg(long now) {
        if (now - lastEggDropTime >= eggIntervalMs) {
            lastEggDropTime = now;
            List<Enemy> alive = new ArrayList<>();
            for (Enemy en : enemies) {
                if (en.alive && !en.flyingIn) {
                    alive.add(en);
                }
            }
            if (!alive.isEmpty()) {
                Enemy chosen = alive.get(rng.nextInt(alive.size()));
                eggs.add(new Egg(chosen.x + chosen.width / 2.0, chosen.y + chosen.height, 0, 4.0));
            }
        }
    }

    private void maybeShooterFire(long now) {
        for (Enemy en : enemies) {
            if (en.alive && !en.flyingIn && "Shooter".equals(en.getTypeName())) {
                if (rng.nextInt(1000) < 5) {
                    double dx = (plane.x + plane.width / 2.0 > en.x) ? 5.0 : -5.0;
                    eggs.add(new Egg(en.x + en.width / 2.0, en.y + en.height / 2.0, dx, 0));
                }
            }
        }
    }

    private void updateBoss(long now) {
        if (freezeActive) {
            return;
        }
        boss.updateMovement(WIDTH);
        List<Egg> newEggs = boss.maybeAttack(now);

        if (newEggs != null) {
            eggs.addAll(newEggs);
        }
    }

    private void updateEggs() {
        Iterator<Egg> it = eggs.iterator();
        while (it.hasNext()) {
            Egg egg = it.next();
            egg.frozen = freezeActive;
            egg.update(WIDTH, HEIGHT);
            if (!egg.alive) {
                it.remove();
                continue;
            }
            if (egg.getBounds().intersects(plane.getBounds())) {
                it.remove();
                explosions.add(new Explosion(plane.x + plane.width / 2.0, plane.y + plane.height / 2.0));
                boolean lostLife = plane.takeHit();
                if (lostLife) {
                    sound.playCrash();
                    if (plane.lives <= 0) {
                        triggerGameOver();
                    }
                }
            }
        }
    }

    private void updatePowerUps() {
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            p.update(HEIGHT);
            if (!p.alive) {
                it.remove();
                continue;
            }
            if (p.getBounds().intersects(plane.getBounds())) {
                it.remove();
                long now = System.currentTimeMillis();
                if (p.type == PowerUp.Type.FREEZE_BOMB) {
                    freezeActive = true;
                    freezeEndTime = now + 3000;
                    for (Egg egg : eggs) {
                        egg.frozen = true;
                    }
                } else {
                    plane.applyPowerUp(p.type, now);
                }
            }
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion ex = it.next();
            ex.update();
            if (!ex.alive) {
                it.remove();
            }
        }
    }

    private void checkLevelProgress() {
        if (boss != null) {
            return;
        }

        boolean anyRemaining = false;
        for (Cell c : cells) {
            if (!c.isSpent()) {
                anyRemaining = true;
                break;
            }
        }

        if (!anyRemaining && enemies.isEmpty() && !victory && !gameOver) {
            startLevel(level + 1);
        }
    }

    private void triggerGameOver() {
        gameOver = true;
        sound.stopMusic();
        sound.playGameOver();
        saveRecordIfNeeded();
    }

    private void triggerVictory() {
        victory = true;
        sound.stopMusic();
        sound.playGameOver();
        sound.playMusicLoop("Chicken Invaders 2 Remastered OST - Ending Theme.wav");
        saveRecordIfNeeded();
    }

    private void saveRecordIfNeeded() {
        if (recordSaved) {
            return;
        }
        recordSaved = true;
        String username = (currentUser != null) ? currentUser.getUsername() : "Guest";
        db.saveGameRecord(username, score, level);
    }

    private void endAndReturnToMenu() {
        saveRecordIfNeeded();
        timer.stop();
        sound.stopMusic();
        gameMain.showMenu();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g);

        for (Cell c : cells) {
            if (c.occupant != null && c.occupant.alive) {
                c.occupant.draw(g);
            }
        }
        if (boss != null) {
            boss.draw(g);
        }
        for (Egg egg : eggs) {
            egg.draw(g);
        }
        for (Bullet b : bullets) {
            b.draw(g);
        }
        for (PowerUp p : powerUps) {
            p.draw(g);
        }
        plane.draw(g);
        for (Explosion ex : explosions) {
            ex.draw(g);
        }

        drawHud(g);

        if (showSettingsOverlay) {
            drawSettingsOverlay(g);
        }
        if (paused) {
            drawOverlay(g, "PAUSED", "Press P to resume");
        }
        if (gameOver) {
            drawOverlay(g, "GAME OVER", "Press ESC to return to the menu");
        }
        if (victory) {
            drawOverlay(g, "YOU WIN!", "Press ESC to return to the menu");
        }
    }

    private void drawSettingsOverlay(Graphics2D g) {
        int bx = WIDTH - 260;
        int by = 10;
        int bw = 250;
        int bh = 118;
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRoundRect(bx, by, bw, bh, 12, 12);
        g.setColor(Color.WHITE);
        g.drawRoundRect(bx, by, bw, bh, 12, 12);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 13f));
        g.drawString("Sound (press M to close)", bx + 10, by + 18);

        g.drawString("1) Music: " + (sound.isMusicOn() ? "On" : "Off"), bx + 10, by + 40);
        g.drawString("2) Shot SFX: " + (sound.isShotOn() ? "On" : "Off"), bx + 10, by + 60);
        g.drawString("3) Crash SFX: " + (sound.isCrashOn() ? "On" : "Off"), bx + 10, by + 80);
        g.drawString("4) Game Over SFX: " + (sound.isGameOverOn() ? "On" : "Off"), bx + 10, by + 100);
    }

    private void drawBackground(Graphics2D g) {
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, WIDTH, HEIGHT, null);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, new Color(10, 10, 40), 0, HEIGHT, new Color(30, 30, 70));
            g.setPaint(gp);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private void drawHud(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
        String user = (currentUser != null) ? currentUser.getUsername() : "Guest";
        g.drawString("Score: " + score, 10, 22);
        g.drawString("Level: " + level, 10, 44);
        g.drawString("Lives: " + plane.lives, 10, 66);
        g.drawString("Player: " + user, 10, 88);
        g.drawString("Bullets/shot: " + plane.bulletsPerShot, 10, 110);

        int textY = 132;
        long now = System.currentTimeMillis();
        if (plane.rapidFireActive) {
            long remaining = Math.max(0, (plane.rapidFireEndTime - now) / 1000);
            g.drawString("Rapid Fire: " + remaining + "s", 10, textY);
            textY += 20;
        }
        if (plane.shieldActive) {
            long remaining = Math.max(0, (plane.shieldEndTime - now) / 1000);
            g.drawString("Shield: " + remaining + "s", 10, textY);
            textY += 20;
        }
        if (freezeActive) {
            long remaining = Math.max(0, (freezeEndTime - now) / 1000);
            g.drawString("Freeze: " + remaining + "s", 10, textY);
        }
    }

    private void drawOverlay(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 48f));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, HEIGHT / 2 - 10);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 18f));
        fm = g.getFontMetrics();
        g.drawString(subtitle, (WIDTH - fm.stringWidth(subtitle)) / 2, HEIGHT / 2 + 30);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < held.length) {
            held[code] = true;
        }

        if (code == KeyEvent.VK_P) {
            if (!gameOver && !victory) {
                paused = !paused;
            }
        } else if (code == KeyEvent.VK_ESCAPE) {
            endAndReturnToMenu();
        } else if (code == KeyEvent.VK_M) {
            showSettingsOverlay = !showSettingsOverlay;
        } else if (showSettingsOverlay) {
            if (code == KeyEvent.VK_1) {
                toggleAndPersistSound(0);
            } else if (code == KeyEvent.VK_2) {
                toggleAndPersistSound(1);
            } else if (code == KeyEvent.VK_3) {
                toggleAndPersistSound(2);
            } else if (code == KeyEvent.VK_4) {
                toggleAndPersistSound(3);
            }
        }
    }

    private void toggleAndPersistSound(int which) {
        if (which == 0) {
            boolean newState = !sound.isMusicOn();
            sound.setMusicOn(newState);
            if (newState) {
                sound.playMusicLoop(victory ? "Chicken Invaders 2 Remastered OST - Ending Theme.wav"
                        : "Chicken Invaders 2 Remastered OST - Main Theme.wav");
            } else {
                sound.stopMusic();
            }
        } else if (which == 1) {
            sound.setShotOn(!sound.isShotOn());
        } else if (which == 2) {
            sound.setCrashOn(!sound.isCrashOn());
        } else if (which == 3) {
            sound.setGameOverOn(!sound.isGameOverOn());
        }

        if (currentUser != null) {
            currentUser.setMusicOn(sound.isMusicOn());
            currentUser.setShotSoundOn(sound.isShotOn());
            currentUser.setCrashSoundOn(sound.isCrashOn());
            currentUser.setGameOverSoundOn(sound.isGameOverOn());
            currentUser.setMusicVolume(sound.getMusicVolume());
            db.updateUser(currentUser);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < held.length) {
            held[code] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}