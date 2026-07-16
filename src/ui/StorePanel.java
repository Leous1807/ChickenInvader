package ui;

import data.User;
import main.GameMain;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class StorePanel extends JPanel {

    private final GameMain gameMain;
    private final JLabel creditsLabel = new JLabel();
    private final JLabel currentPlaneLabel = new JLabel();
    private final JPanel listPanel = new JPanel();
    private Image backgroundImage;

    private static final String[][] PLANES = {
            {"Default", "0", "5", "300", "3", "-"},
            {"Fast", "5000", "7", "250", "3", "-"},
            {"Heavy", "8000", "4", "200", "5", "-"},
            {"Sniper", "10000", "5", "150", "3", "2x damage to bosses"}
    };

    public StorePanel(GameMain gameMain) {
        this.gameMain = gameMain;
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(15, 15, 35));

        try {
            backgroundImage = ImageIO.read(new File("resources/background/background2.jpg"));
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        JLabel title = new JLabel("Store", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 60, 0, 60));
        add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        creditsLabel.setForeground(Color.YELLOW);
        creditsLabel.setAlignmentX(CENTER_ALIGNMENT);
        currentPlaneLabel.setForeground(Color.WHITE);
        currentPlaneLabel.setAlignmentX(CENTER_ALIGNMENT);
        JButton back = new JButton("Back to menu");
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.addActionListener(e -> gameMain.showMenu());
        south.add(creditsLabel);
        south.add(currentPlaneLabel);
        south.add(Box.createVerticalStrut(8));
        south.add(back);
        add(south, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void refresh() {
        listPanel.removeAll();
        User u = gameMain.getCurrentUser();

        if (u == null) {
            JLabel warn = new JLabel("Please log in to use the store.");
            warn.setForeground(Color.PINK);
            listPanel.add(warn);
            creditsLabel.setText("");
            currentPlaneLabel.setText("");
        } else {
            for (int i = 0; i < PLANES.length; i = i + 1) {
                String[] plane = PLANES[i];
                listPanel.add(buildRow(u, plane));
                listPanel.add(Box.createVerticalStrut(6));
            }
            creditsLabel.setText("Available credits (your high score): " + u.getHighScore());
            currentPlaneLabel.setText("Currently equipped: " + u.getSelectedPlane());
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildRow(User u, String[] plane) {
        String name = plane[0];
        int cost = Integer.parseInt(plane[1]);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(650, 60));

        JLabel imageLabel = new JLabel();
        try {
            String path = "resources/airplan/1.png";
            if (name.equals("Fast") == true) {
                path = "resources/airplan/2.png";
            } else if (name.equals("Heavy") == true) {
                path = "resources/airplan/3.png";
            } else if (name.equals("Sniper") == true) {
                path = "resources/airplan/4.png";
            }
            Image img = ImageIO.read(new File(path)).getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
        }
        row.add(imageLabel, BorderLayout.WEST);

        String desc = String.format("%-8s cost:%-6d speed:%-3s fireRate:%-4sms lives:%-2s %s",
                name, cost, plane[2], plane[3], plane[4], plane[5]);
        JLabel label = new JLabel(desc);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.PLAIN, 13));
        row.add(label, BorderLayout.CENTER);

        boolean isEquipped = name.equals(u.getSelectedPlane());
        String buttonText = "Buy & Equip";
        if (isEquipped == true) {
            buttonText = "Equipped";
        }
        JButton buy = new JButton(buttonText);
        buy.setEnabled(isEquipped == false);
        buy.addActionListener(e -> {
            if (cost > 0) {
                if (u.getHighScore() < cost) {
                    JOptionPane.showMessageDialog(this, "Not enough points for the " + name + " plane.");
                    return;
                }
            }
            if (cost > 0) {
                u.setHighScore(u.getHighScore() - cost);
            }
            u.setSelectedPlane(name);
            gameMain.getDb().updateUser(u);
            refresh();
        });
        row.add(buy, BorderLayout.EAST);
        return row;
    }
}