package ui;

import data.GameRecord;
import main.GameMain;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HighScorePanel extends JPanel {

    private final GameMain gameMain;
    private final DefaultTableModel model;
    private final JTable table;

    public HighScorePanel(GameMain gameMain) {
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 35));

        JLabel title = new JLabel("High Scores", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Username", "Score", "Level Reached", "Date/Time"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        add(scroll, BorderLayout.CENTER);

        JButton back = new JButton("Back to menu");
        back.addActionListener(e -> gameMain.showMenu());
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        List<GameRecord> board = gameMain.getDb().getHighScoreBoard();
        for (GameRecord r : board) {
            model.addRow(new Object[]{r.username, r.score, r.levelReached, r.timestamp});
        }
    }
}