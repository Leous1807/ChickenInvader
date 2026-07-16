package data;

public class GameRecord {
    public String username;
    public int score;
    public int levelReached;
    public String timestamp;
    public String soundSettingsSummary;

    public GameRecord(String username, int score, int levelReached,
                      String timestamp, String soundSettingsSummary) {
        this.username = username;
        this.score = score;
        this.levelReached = levelReached;
        this.timestamp = timestamp;
        this.soundSettingsSummary = soundSettingsSummary;
    }

    public String toDataLine() {
        return username + "|" + score + "|" + levelReached + "|" + timestamp + "|" + soundSettingsSummary;
    }

    public static GameRecord fromDataLine(String line) {
        String[] parts = line.split("\\|", -1);
        String user = parts[0];
        int points = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);
        String time = parts[3];
        String settings = parts[4];
        return new GameRecord(user, points, level, time, settings);
    }
}