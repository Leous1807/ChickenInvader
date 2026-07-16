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



    public String getSelectedPlane() {

        return selectedPlane;

    }



    public void setSelectedPlane(String selectedPlane) {

        this.selectedPlane = selectedPlane;

    }



    public String toDataLine() {

        return username + "|" + password + "|" + highScore + "|" + lastLevel + "|" + musicOn + "|" + shotSoundOn + "|" + crashSoundOn + "|" + gameOverSoundOn + "|" + selectedPlane;

    }



    public static User fromDataLine(String line) {

        String[] parts = line.split("\\|", -1);

        User user = new User(parts[0], parts[1]);

        user.highScore = Integer.parseInt(parts[2]);

        user.lastLevel = Integer.parseInt(parts[3]);

        user.musicOn = Boolean.parseBoolean(parts[4]);

        user.shotSoundOn = Boolean.parseBoolean(parts[5]);

        user.crashSoundOn = Boolean.parseBoolean(parts[6]);

        user.gameOverSoundOn = Boolean.parseBoolean(parts[7]);

        if (parts.length > 8) {

            user.selectedPlane = parts[8];

        }

        return user;

    }

}

