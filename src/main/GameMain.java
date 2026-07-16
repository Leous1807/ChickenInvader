package main;

import audio.SoundManager;
import data.DatabaseManager;
import data.User;
import game.GamePanel;
import ui.*;

import javax.swing.*;
import java.awt.*;

public class GameMain extends JFrame {

    public static final String CARD_MENU = "menu";
    public static final String CARD_LOGIN = "login";
    public static final String CARD_REGISTER = "register";
    public static final String CARD_HIGHSCORES = "highscores";
    public static final String CARD_SETTINGS = "settings";
    public static final String CARD_HOWTO = "howto";
    public static final String CARD_STORE = "store";
    public static final String CARD_GAME = "game";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    private final DatabaseManager db = new DatabaseManager();
    private final SoundManager sound = new SoundManager();

    private User currentUser;

    private MainMenu mainMenu;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private HighScorePanel highScorePanel;
    private SettingsPanel settingsPanel;
    private HowToPlayPanel howToPlayPanel;
    private StorePanel storePanel;
    private GamePanel gamePanel;

    public GameMain() {
        super("Chicken Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        mainMenu = new MainMenu(this);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        highScorePanel = new HighScorePanel(this);
        settingsPanel = new SettingsPanel(this);
        howToPlayPanel = new HowToPlayPanel(this);
        storePanel = new StorePanel(this);

        container.add(mainMenu, CARD_MENU);
        container.add(loginPanel, CARD_LOGIN);
        container.add(registerPanel, CARD_REGISTER);
        container.add(highScorePanel, CARD_HIGHSCORES);
        container.add(settingsPanel, CARD_SETTINGS);
        container.add(howToPlayPanel, CARD_HOWTO);
        container.add(storePanel, CARD_STORE);

        add(container);
        pack();
        setLocationRelativeTo(null);

        sound.playMusicLoop("Chicken Invaders 2 Remastered OST - Main Theme.wav");
    }

    public DatabaseManager getDb() {
        return db;
    }

    public SoundManager getSound() {
        return sound;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            sound.setMusicOn(user.isMusicOn());
            sound.setShotOn(user.isShotSoundOn());
            sound.setCrashOn(user.isCrashSoundOn());
            sound.setGameOverOn(user.isGameOverSoundOn());
        }
    }

    public void logout() {
        currentUser = null;
    }

    public void showMenu() {
        if (currentUser != null) {
            currentUser = db.findUser(currentUser.getUsername());
        }
        mainMenu.refresh();
        cardLayout.show(container, CARD_MENU);
        sound.playMusicLoop("Chicken Invaders 2 Remastered OST - Main Theme.wav");
    }

    public void showLogin(boolean startGameAfterLogin) {
        loginPanel.setStartGameAfterLogin(startGameAfterLogin);
        loginPanel.reset();
        cardLayout.show(container, CARD_LOGIN);
    }

    public void showRegister(boolean startGameAfterLogin) {
        registerPanel.setStartGameAfterLogin(startGameAfterLogin);
        registerPanel.reset();
        cardLayout.show(container, CARD_REGISTER);
    }

    public void showHighScores() {
        highScorePanel.refresh();
        cardLayout.show(container, CARD_HIGHSCORES);
    }

    public void showSettings() {
        settingsPanel.refresh();
        cardLayout.show(container, CARD_SETTINGS);
    }

    public void showHowToPlay() {
        cardLayout.show(container, CARD_HOWTO);
    }

    public void showStore() {
        storePanel.refresh();
        cardLayout.show(container, CARD_STORE);
    }

    public void startNewGame() {
        if (gamePanel != null) {
            container.remove(gamePanel);
        }
        gamePanel = new GamePanel(this, db, sound, currentUser);
        container.add(gamePanel, CARD_GAME);
        cardLayout.show(container, CARD_GAME);
        pack();
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain gm = new GameMain();
            gm.setVisible(true);
        });
    }
}