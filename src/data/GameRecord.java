package data;

public class GameRecord {
    public String username;
    public int score;
    public int levelReached;
    public String timestamp;

    public GameRecord(String username, int score, int levelReached, String timestamp) {
        this.username = username;
        this.score = score;
        this.levelReached = levelReached;
        this.timestamp = timestamp;
    }

    public String toDataLine() {
        return username + "|" + score + "|" + levelReached + "|" + timestamp;
    }

    public static GameRecord fromDataLine(String line) {
        String[] parts = line.split("\\|", -1);
        String user = parts[0];
        int points = Integer.parseInt(parts[1]);
        int level = Integer.parseInt(parts[2]);
        String time = parts[3];
        return new GameRecord(user, points, level, time);
    }
}