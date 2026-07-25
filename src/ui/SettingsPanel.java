package ui;

import data.User;
import main.GameMain;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final GameMain gameMain;
    private final JCheckBox musicBox = new JCheckBox("Background Music");
    private final JSlider volumeSlider = new JSlider(0, 100, 80);
    private final JCheckBox shotBox = new JCheckBox("Shot Sound Effect");
    private final JCheckBox crashBox = new JCheckBox("Crash / Explosion Sound Effect");
    private final JCheckBox gameOverBox = new JCheckBox("Game Over / Win Sound");
    private final JLabel statusLabel = new JLabel(" ");

    public SettingsPanel(GameMain gameMain) {
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 35));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(20));

        JCheckBox[] boxes = new JCheckBox[]{musicBox, shotBox, crashBox, gameOverBox};
        for (JCheckBox box : boxes) {
            box.setForeground(Color.WHITE);
            box.setOpaque(false);
            box.setAlignmentX(CENTER_ALIGNMENT);
            box.setFont(new Font("SansSerif", Font.PLAIN, 16));
        }

        form.add(musicBox);
        form.add(Box.createVerticalStrut(6));

        JPanel volumePanel = new JPanel();
        volumePanel.setOpaque(false);
        JLabel volLabel = new JLabel("Music Volume: ");
        volLabel.setForeground(Color.WHITE);
        volLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(180, 24));

        volumePanel.add(volLabel);
        volumePanel.add(volumeSlider);
        volumePanel.setAlignmentX(CENTER_ALIGNMENT);
        form.add(volumePanel);
        form.add(Box.createVerticalStrut(10));

        form.add(shotBox);
        form.add(Box.createVerticalStrut(8));
        form.add(crashBox);
        form.add(Box.createVerticalStrut(8));
        form.add(gameOverBox);
        form.add(Box.createVerticalStrut(20));

        JButton save = new JButton("Save");
        save.setAlignmentX(CENTER_ALIGNMENT);
        save.addActionListener(e -> doSave());
        form.add(save);

        form.add(Box.createVerticalStrut(10));
        JButton back = new JButton("Back to menu");
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.addActionListener(e -> gameMain.showMenu());
        form.add(back);

        form.add(Box.createVerticalStrut(10));
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        form.add(statusLabel);

        add(form);
    }

    public void refresh() {
        musicBox.setSelected(gameMain.getSound().isMusicOn());
        shotBox.setSelected(gameMain.getSound().isShotOn());
        crashBox.setSelected(gameMain.getSound().isCrashOn());
        gameOverBox.setSelected(gameMain.getSound().isGameOverOn());

        User u = gameMain.getCurrentUser();
        if (u != null) {
            volumeSlider.setValue((int)u.getMusicVolume());
        } else {
            volumeSlider.setValue(80);
        }
        statusLabel.setText(" ");
    }

    private void doSave() {
        boolean musicOn = musicBox.isSelected();
        int vol = volumeSlider.getValue();

        gameMain.getSound().setMusicOn(musicOn);
        gameMain.getSound().setShotOn(shotBox.isSelected());
        gameMain.getSound().setCrashOn(crashBox.isSelected());
        gameMain.getSound().setGameOverOn(gameOverBox.isSelected());
        gameMain.getSound().setMusicVolume(vol);

        if (!musicOn) {
            gameMain.getSound().stopMusic();
        } else if (!gameMain.getSound().isMusicOn()) {
            gameMain.getSound().playMusicLoop("Chicken Invaders 2 Remastered OST - Main Theme.wav");
        }

        User u = gameMain.getCurrentUser();
        if (u != null) {
            u.setMusicOn(musicOn);
            u.setShotSoundOn(shotBox.isSelected());
            u.setCrashSoundOn(crashBox.isSelected());
            u.setGameOverSoundOn(gameOverBox.isSelected());
            u.setMusicVolume(vol);

            gameMain.getDb().updateUser(u);
        }
        statusLabel.setText("Saved!");
    }
}