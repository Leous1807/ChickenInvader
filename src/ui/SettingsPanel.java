package ui;

import data.User;
import main.GameMain;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final GameMain gameMain;
    private final JCheckBox musicBox = new JCheckBox("Background Music");
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
        for (int i = 0; i < boxes.length; i = i + 1) {
            JCheckBox box = boxes[i];
            box.setForeground(Color.WHITE);
            box.setOpaque(false);
            box.setAlignmentX(CENTER_ALIGNMENT);
            box.setFont(new Font("SansSerif", Font.PLAIN, 16));
            form.add(box);
            form.add(Box.createVerticalStrut(8));
        }

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
        statusLabel.setText(" ");
    }

    private void doSave() {
        gameMain.getSound().setMusicOn(musicBox.isSelected());
        gameMain.getSound().setShotOn(shotBox.isSelected());
        gameMain.getSound().setCrashOn(crashBox.isSelected());
        gameMain.getSound().setGameOverOn(gameOverBox.isSelected());

        User u = gameMain.getCurrentUser();
        if (u != null) {
            u.setMusicOn(musicBox.isSelected());
            u.setShotSoundOn(shotBox.isSelected());
            u.setCrashSoundOn(crashBox.isSelected());
            u.setGameOverSoundOn(gameOverBox.isSelected());
            gameMain.getDb().updateUser(u);
        }
        statusLabel.setText("Saved!");
    }
}