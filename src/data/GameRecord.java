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
}