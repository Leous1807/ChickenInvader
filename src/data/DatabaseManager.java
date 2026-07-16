package data;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DatabaseManager {

    private static final String DATA_DIR = "data";
    private static final String DB_FILE_PATH = DATA_DIR + "/game_database.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public DatabaseManager() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (Connection conn = getConnection()) {
            if (conn != null) {
                createTables(conn);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    private void createTables(Connection conn) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "high_score INTEGER DEFAULT 0, " +
                "last_level INTEGER DEFAULT 0, " +
                "selected_plane TEXT DEFAULT 'Default'" +
                ");";

        String createGamesTable = "CREATE TABLE IF NOT EXISTS game_records (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "score INTEGER DEFAULT 0, " +
                "level_reached INTEGER DEFAULT 0, " +
                "timestamp TEXT NOT NULL, " +
                "sound_summary TEXT NOT NULL, " +
                "FOREIGN KEY (username) REFERENCES users(username)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createGamesTable);

            try {
                stmt.execute("ALTER TABLE users ADD COLUMN selected_plane TEXT DEFAULT 'Default'");
            } catch (SQLException ignored) {
            }
        }
    }

    public synchronized boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public synchronized User registerUser(String username, String password) {
        if (userExists(username)) {
            return null;
        }

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            return new User(username, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public synchronized User login(String username, String password) {
        User user = findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User findUser(String username) {
        String sql = "SELECT username, password, high_score, last_level, selected_plane FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(rs.getString("username"), rs.getString("password"));
                    user.setHighScore(rs.getInt("high_score"));
                    user.setLastLevel(rs.getInt("last_level"));
                    String plane = rs.getString("selected_plane");
                    if (plane != null) {
                        user.setSelectedPlane(plane);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public synchronized void updateUser(User updated) {
        String sql = "UPDATE users SET password = ?, high_score = ?, last_level = ?, selected_plane = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, updated.getPassword());
            pstmt.setInt(2, updated.getHighScore());
            pstmt.setInt(3, updated.getLastLevel());
            pstmt.setString(4, updated.getSelectedPlane());
            pstmt.setString(5, updated.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void saveGameRecord(String username, int score, int levelReached,
                                            boolean musicOn, boolean shotOn,
                                            boolean crashOn, boolean gameOverOn) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(currentDate);

        String soundSummary = "M" + (musicOn ? "1" : "0") +
                "S" + (shotOn ? "1" : "0") +
                "C" + (crashOn ? "1" : "0") +
                "G" + (gameOverOn ? "1" : "0");

        String insertGameSql = "INSERT INTO game_records(username, score, level_reached, timestamp, sound_summary) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertGameSql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.setInt(3, levelReached);
            pstmt.setString(4, timestamp);
            pstmt.setString(5, soundSummary);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        User user = findUser(username);
        if (user != null) {
            boolean changed = false;
            if (score > user.getHighScore()) {
                user.setHighScore(score);
                changed = true;
            }
            if (levelReached > user.getLastLevel()) {
                user.setLastLevel(levelReached);
                changed = true;
            }
            if (changed) {
                updateUser(user);
            }
        }
    }

    public synchronized List<GameRecord> loadAllRecords() {
        List<GameRecord> list = new ArrayList<>();
        String sql = "SELECT username, score, level_reached, timestamp, sound_summary FROM game_records";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GameRecord record = new GameRecord(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("level_reached"),
                        rs.getString("timestamp"),
                        rs.getString("sound_summary")
                );
                list.add(record);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public synchronized List<GameRecord> getHighScoreBoard() {
        List<GameRecord> list = new ArrayList<>();
        String sql = "SELECT r.username, r.score, r.level_reached, r.timestamp, r.sound_summary " +
                "FROM game_records r " +
                "INNER JOIN (" +
                "   SELECT username, MAX(score) as max_score " +
                "   FROM game_records " +
                "   GROUP BY username" +
                ") temp ON r.username = temp.username AND r.score = temp.max_score " +
                "ORDER BY r.score DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                GameRecord record = new GameRecord(
                        rs.getString("username"),
                        rs.getInt("score"),
                        rs.getInt("level_reached"),
                        rs.getString("timestamp"),
                        rs.getString("sound_summary")
                );
                list.add(record);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }
}