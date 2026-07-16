package ui;

import main.GameMain;

import javax.swing.*;
import java.awt.*;

public class HowToPlayPanel extends JPanel {

    public HowToPlayPanel(GameMain gameMain) {
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 35));

        JLabel title = new JLabel("How to Play", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setFont(new Font("Monospaced", Font.PLAIN, 15));
        text.setMargin(new Insets(10, 10, 10, 10));
        text.setText(
                "GOAL\n" +
                        "Fly your plane, shoot the chickens before their eggs hit you,\n" +
                        "and clear all 8 levels (two of which are giant boss fights).\n\n" +
                        "CONTROLS\n" +
                        "  Right / D  -> move right\n" +
                        "  Left  / A  -> move left\n" +
                        "  Up    / W  -> move up\n" +
                        "  Down  / S  -> move down\n" +
                        "  Space      -> shoot\n" +
                        "  P          -> pause / resume\n" +
                        "  Esc        -> end the game and return to the main menu\n" +
                        "  M          -> toggle the in-game sound settings overlay\n\n" +
                        "POWER-UPS (20% drop chance per kill)\n" +
                        "  Rapid Fire   - faster shooting for 8 seconds\n" +
                        "  Freeze Bomb  - freezes all enemies and eggs for 3 seconds\n" +
                        "  Extra Life   - +1 life, up to a maximum of 5\n" +
                        "  Shield       - 10 seconds of immunity to eggs\n" +
                        "  Add Fire     - permanently adds one more simultaneous bullet\n\n" +
                        "LIVES\n" +
                        "  You start with 3 lives. They do NOT refill between levels.\n" +
                        "  Losing all your lives ends the game.\n\n" +
                        "SCORING\n" +
                        "  Normal 10, Fast 15, Zigzag 20, Shooter 25 points per kill.\n" +
                        "  +200 for clearing a regular level, +500 for the level 4 boss,\n" +
                        "  +1000 and a Victory screen for defeating the final boss."
        );

        JScrollPane scroll = new JScrollPane(text);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        add(scroll, BorderLayout.CENTER);

        JButton back = new JButton("Back to menu");
        back.addActionListener(e -> gameMain.showMenu());
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }
}