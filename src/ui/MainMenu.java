package ui;

import main.GameMain;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {

    private final GameMain gameMain;
    private final JLabel welcomeLabel;
    private final JButton loginLogoutButton;

    public MainMenu(GameMain gameMain) {
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 35));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CHICKEN INVADERS");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(CENTER_ALIGNMENT);

        welcomeLabel = new JLabel(" ");
        welcomeLabel.setForeground(Color.LIGHT_GRAY);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);

        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(welcomeLabel);
        inner.add(Box.createVerticalStrut(30));

        inner.add(menuButton("New Game", e -> {
            if (gameMain.getCurrentUser() == null) {
                gameMain.showLogin(true);
            } else {
                gameMain.startNewGame();
            }
        }));
        inner.add(menuButton("High Scores", e -> gameMain.showHighScores()));
        inner.add(menuButton("Settings", e -> gameMain.showSettings()));
        inner.add(menuButton("How to Play", e -> gameMain.showHowToPlay()));
        inner.add(menuButton("Store", e -> gameMain.showStore()));

        loginLogoutButton = menuButton("Login", e -> handleLoginLogout());
        inner.add(loginLogoutButton);

        inner.add(menuButton("Exit", e -> System.exit(0)));

        add(inner);

        refresh();
    }

    private JButton menuButton(String text, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(220, 40));
        b.setFont(new Font("SansSerif", Font.PLAIN, 16));
        b.addActionListener(action);
        return b;
    }

    private void handleLoginLogout() {
        if (gameMain.getCurrentUser() == null) {
            gameMain.showLogin(false);
        } else {
            gameMain.logout();
            refresh();
        }
    }

    public void refresh() {
        if (gameMain.getCurrentUser() != null) {
            welcomeLabel.setText("Logged in as: " + gameMain.getCurrentUser().getUsername());
            loginLogoutButton.setText("Logout");
        } else {
            welcomeLabel.setText("Not logged in — New Game will ask you to sign in");
            loginLogoutButton.setText("Login");
        }
    }
}