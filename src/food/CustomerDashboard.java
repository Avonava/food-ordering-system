package food;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerDashboard extends JFrame {
    private String username;
    private int userId;
    private Connection conn;
    private JPanel menuPanel;
    private JTextArea reportArea;

    // Colors
    private final Color PRIMARY_COLOR = new Color(40, 167, 69);
    private final Color SECONDARY_COLOR = new Color(33, 37, 41);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(73, 80, 87);

    public CustomerDashboard(String username) {
        this.username = username;

        setTitle("Customer Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        connectToDatabase();

        userId = fetchUserId(username);
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "User ID not found. Closing dashboard.");
            dispose();
            return;
        }

        // Welcome label and logout button at the top
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, SECONDARY_COLOR, Color.WHITE);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logged out successfully.");
            dispose();
             new LoginPage().setVisible(true);
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Menu items panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        loadMenuItems();

        // Bottom panel for report submission and my orders button
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        reportArea = new JTextArea(3, 20);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEmptyBorder(), 
            "Your Feedback",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
        bottomPanel.add(reportScroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BACKGROUND_COLOR);

        JButton btnSubmitReport = new JButton("Submit Feedback");
        styleButton(btnSubmitReport, PRIMARY_COLOR, Color.WHITE);
        btnSubmitReport.addActionListener(e -> submitReport());
        btnPanel.add(btnSubmitReport);

        JButton btnMyOrders = new JButton("My Orders");
        styleButton(btnMyOrders, SECONDARY_COLOR, Color.WHITE);
        btnMyOrders.addActionListener(e -> showMyOrders());
        btnPanel.add(btnMyOrders);

        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color bgColor, Color textColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker()),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            dispose();
        }
    }

    private int fetchUserId(String username) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                rs.close();
                pst.close();
                return id;
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching user ID: " + e.getMessage());
        }
        return -1;
    }

    private void loadMenuItems() {
        menuPanel.removeAll();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_id, name, description, price, image_path FROM menu_items");

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");

                JPanel itemPanel = new JPanel(new BorderLayout(15, 15));
                itemPanel.setBackground(CARD_COLOR);
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230)),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                ImageIcon icon = null;
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon originalIcon = new ImageIcon(imagePath);
                    Image img = originalIcon.getImage();
                    Image scaledImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImg);
                } else {
                    // Create a placeholder image
                    BufferedImage placeholder = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = placeholder.createGraphics();
                    g2d.setColor(new Color(233, 236, 239));
                    g2d.fillRect(0, 0, 120, 120);
                    g2d.setColor(new Color(173, 181, 189));
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    g2d.drawString("No Image", 30, 60);
                    g2d.dispose();
                    icon = new ImageIcon(placeholder);
                }

                JLabel imageLabel = new JLabel(icon);
                imageLabel.setBorder(BorderFactory.createLineBorder(new Color(233, 236, 239)));
                itemPanel.add(imageLabel, BorderLayout.WEST);

                JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
                infoPanel.setBackground(CARD_COLOR);
                infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
                nameLabel.setForeground(SECONDARY_COLOR);
                infoPanel.add(nameLabel, BorderLayout.NORTH);

                JLabel descLabel = new JLabel("<html>" + description + "</html>");
                descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                descLabel.setForeground(TEXT_COLOR);
                infoPanel.add(descLabel, BorderLayout.CENTER);

                JLabel priceLabel = new JLabel("$" + String.format("%.2f", price));
                priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                priceLabel.setForeground(PRIMARY_COLOR);
                infoPanel.add(priceLabel, BorderLayout.SOUTH);

                itemPanel.add(infoPanel, BorderLayout.CENTER);

                JButton btnOrder = new JButton("ORDER NOW");
                styleButton(btnOrder, PRIMARY_COLOR, Color.WHITE);
                btnOrder.addActionListener(e -> placeOrder(itemId));
                itemPanel.add(btnOrder, BorderLayout.EAST);

                menuPanel.add(itemPanel);
                menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + e.getMessage());
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void placeOrder(int itemId) {
        Object[] options = {"Confirm Order", "Cancel"};
        int confirm = JOptionPane.showOptionDialog(this,
            "Are you sure you want to place this order?",
            "Confirm Order",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO my_orders (user_id, item_id, order_date, status) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.setString(3, orderDate);
            stmt.setString(4, "Pending");
            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(this, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error placing order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitReport() {
        String content = reportArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your feedback.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO report (user_id, content) VALUES (?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, content);
            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(this, "Feedback submitted. Thank you!", "Success", JOptionPane.INFORMATION_MESSAGE);
            reportArea.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting feedback: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMyOrders() {
        JDialog ordersDialog = new JDialog(this, "My Orders", true);
        ordersDialog.setSize(700, 500);
        ordersDialog.setLocationRelativeTo(this);
        ordersDialog.getContentPane().setBackground(BACKGROUND_COLOR);

        String[] columns = {"Order ID", "Item Name", "Order Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable ordersTable = new JTable(model);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ordersTable.setRowHeight(30);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTable.setShowGrid(false);
        ordersTable.setIntercellSpacing(new Dimension(0, 0));
        ordersTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        ordersTable.setSelectionForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        ordersDialog.add(scrollPane, BorderLayout.CENTER);

        try {
            String sql = "SELECT o.order_id, m.name, o.order_date, o.status " +
                         "FROM my_orders o JOIN menu_items m ON o.item_id = m.item_id " +
                         "WHERE o.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String itemName = rs.getString("name");
                String orderDate = rs.getString("order_date");
                String status = rs.getString("status");
                
                // Style status text
                String styledStatus = "<html><font color='" + 
                    (status.equals("Pending") ? "#FFC107" : 
                     status.equals("Completed") ? "#28A745" : "#DC3545") + 
                    "'>" + status + "</font></html>";
                
                model.addRow(new Object[]{orderId, itemName, orderDate, styledStatus});
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching orders: " + e.getMessage());
        }

        ordersDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            CustomerDashboard dashboard = new CustomerDashboard("JohnDoe");
            dashboard.setVisible(true);
        });
    }
}