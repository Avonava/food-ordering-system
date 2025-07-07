package food;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private String adminUsername;

    public AdminDashboard(String username) {
        this.adminUsername = username;

        setTitle("Admin Dashboard - Online Food Ordering System");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use a modern flat look and feel (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback
        }

        // Background panel with subtle gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(58, 123, 213);    // nice blue
                Color color2 = new Color(0, 210, 255);     // bright cyan
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title label
        JLabel welcomeLabel = new JLabel("Welcome, Admin " + adminUsername + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        backgroundPanel.add(welcomeLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage Your Online Food Ordering System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 235, 255, 200));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        backgroundPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1; // reset for buttons
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 50;
        gbc.ipady = 15;

        // Create styled buttons
        JButton btnManageMenu = createModernButton("Manage Menu");
        gbc.gridx = 0; gbc.gridy = 2;
        backgroundPanel.add(btnManageMenu, gbc);

        JButton btnViewOrders = createModernButton("View Orders");
        gbc.gridx = 1; gbc.gridy = 2;
        backgroundPanel.add(btnViewOrders, gbc);

        JButton btnManageUsers = createModernButton("Manage Users");
        gbc.gridx = 0; gbc.gridy = 3;
        backgroundPanel.add(btnManageUsers, gbc);

        JButton btnViewReports = createModernButton("View Reports");
        gbc.gridx = 1; gbc.gridy = 3;
        backgroundPanel.add(btnViewReports, gbc);

        JButton btnLogout = createModernButton("Logout");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        backgroundPanel.add(btnLogout, gbc);

        // Button actions
        btnManageMenu.addActionListener(e -> {
            ManageMenu menuWindow = new ManageMenu();
            menuWindow.setVisible(true);
        });

        btnViewOrders.addActionListener(e -> {
            ViewOrders ordersWindow = new ViewOrders();
            ordersWindow.setVisible(true);
        });

        btnManageUsers.addActionListener(e -> {
            ManageUsers usersWindow = new ManageUsers();
            usersWindow.setVisible(true);
        });

        btnViewReports.addActionListener(e -> {
            ViewReports reportsWindow = new ViewReports();
            reportsWindow.setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 123, 255));
        button.setBorder(new RoundedBorder(15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 90, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 123, 255));
            }
        });

        return button;
    }

    // Rounded border class
    static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius / 2;
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard("Nova").setVisible(true);
        });
    }
}
