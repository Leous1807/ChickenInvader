package ui;

import data.User;
import main.GameMain;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final GameMain gameMain;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JLabel statusLabel = new JLabel(" ");
    private boolean startGameAfterLogin = false;

    public LoginPanel(GameMain gameMain) {
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new GridBagLayout());
        setBackground(new Color(15, 15, 35));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx = 0;
        c.gridy = 0;

        JLabel title = new JLabel("Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        c.gridwidth = 2;
        form.add(title, c);

        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        form.add(label("Username:"), c);
        c.gridx = 1;
        form.add(usernameField, c);

        c.gridx = 0;
        c.gridy = c.gridy + 1;
        form.add(label("Password:"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        c.gridx = 0;
        c.gridy = c.gridy + 1;
        c.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        form.add(loginBtn, c);

        c.gridy = c.gridy + 1;
        JButton registerBtn = new JButton("Create an account");
        registerBtn.addActionListener(e -> gameMain.showRegister(startGameAfterLogin));
        form.add(registerBtn, c);

        c.gridy = c.gridy + 1;
        JButton backBtn = new JButton("Back to menu");
        backBtn.addActionListener(e -> gameMain.showMenu());
        form.add(backBtn, c);

        c.gridy = c.gridy + 1;
        statusLabel.setForeground(Color.PINK);
        form.add(statusLabel, c);

        add(form);
    }

    public void setStartGameAfterLogin(boolean startGameAfterLogin) {
        this.startGameAfterLogin = startGameAfterLogin;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() == true || password.isEmpty() == true) {
            statusLabel.setText("Please fill in both fields.");
            return;
        }
        User u = gameMain.getDb().login(username, password);
        if (u == null) {
            statusLabel.setText("Invalid username or password.");
            return;
        }
        gameMain.setCurrentUser(u);
        if (startGameAfterLogin == true) {
            gameMain.startNewGame();
        } else {
            gameMain.showMenu();
        }
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}