package data;

public class User {

    private String username;
    private String password;
    private int highScore;
    private int lastLevel;
    private boolean musicOn = true;
    private boolean shotSoundOn = true;
    private boolean crashSoundOn = true;
    private boolean gameOverSoundOn = true;
    private float musicVolume = 0.3f;
    private String selectedPlane = "Default";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.lastLevel = 1;
    }

    public User(String username, String password, int highScore, int lastLevel,
                boolean musicOn, boolean shotSoundOn, boolean crashSoundOn,
                boolean gameOverSoundOn, String selectedPlane) {
        this.username = username;
        this.password = password;
        this.highScore = highScore;
        this.lastLevel = lastLevel;
        this.musicOn = musicOn;
        this.shotSoundOn = shotSoundOn;
        this.crashSoundOn = crashSoundOn;
        this.gameOverSoundOn = gameOverSoundOn;
        this.selectedPlane = selectedPlane;
    }

    public User(String username, String password, int highScore, int lastLevel,
                boolean musicOn, boolean shotSoundOn, boolean crashSoundOn,
                boolean gameOverSoundOn, float musicVolume, String selectedPlane) {
        this.username = username;
        this.password = password;
        this.highScore = highScore;
        this.lastLevel = lastLevel;
        this.musicOn = musicOn;
        this.shotSoundOn = shotSoundOn;
        this.crashSoundOn = crashSoundOn;
        this.gameOverSoundOn = gameOverSoundOn;
        this.musicVolume = musicVolume;
        this.selectedPlane = selectedPlane;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getLastLevel() {
        return lastLevel;
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void setMusicOn(boolean v) {
        musicOn = v;
    }

    public boolean isShotSoundOn() {
        return shotSoundOn;
    }

    public void setShotSoundOn(boolean v) {
        shotSoundOn = v;
    }

    public boolean isCrashSoundOn() {
        return crashSoundOn;
    }

    public void setCrashSoundOn(boolean v) {
        crashSoundOn = v;
    }

    public boolean isGameOverSoundOn() {
        return gameOverSoundOn;
    }

    public void setGameOverSoundOn(boolean v) {
        gameOverSoundOn = v;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public String getSelectedPlane() {
        return selectedPlane;
    }

    public void setSelectedPlane(String selectedPlane) {
        this.selectedPlane = selectedPlane;
    }
}