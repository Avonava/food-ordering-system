package food;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;

public class ViewOrders extends JFrame {
    private Connection conn;
    private DefaultTableModel model;
    private JTable table;

    public ViewOrders() {
        setTitle("🍽️ View Orders - Aman Lounge");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connectToDatabase();

        // Custom fonts and colors
        Font titleFont = new Font("SansSerif", Font.BOLD, 22);
        Color backgroundColor = new Color(240, 248, 255);
        Color headerColor = new Color(70, 130, 180);
        Color rowAlternate = new Color(230, 240, 250);

        // Title label
        JLabel titleLabel = new JLabel("📋 View and Update Orders");
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(headerColor);
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "User ID", "Item ID", "Order Date", "Current Status", "Update Status"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }

            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? getBackground() : rowAlternate);
                } else {
                    c.setBackground(new Color(184, 207, 229));
                }
                return c;
            }
        };
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);

        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(
                new JComboBox<>(new String[]{"Confirmed", "Cancelled", "Delivered", "Pending"})));

        loadOrders();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(backgroundColor);
        add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("✅ Apply Updates");
        styleButton(updateButton, new Color(46, 139, 87));

        JButton logoutButton = new JButton("🚪 Logout");
        styleButton(logoutButton, new Color(220, 20, 60));

        updateButton.addActionListener(e -> applyUpdates());
        logoutButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(updateButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(backgroundColor);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            dispose();
        }
    }

    private void loadOrders() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT order_id, user_id, item_id, order_date, status FROM my_orders");

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                int itemId = rs.getInt("item_id");
                Timestamp orderDate = rs.getTimestamp("order_date");
                String status = rs.getString("status");

                model.addRow(new Object[]{
                        orderId,
                        userId,
                        itemId,
                        orderDate.toString(),
                        status,
                        status
                });
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void applyUpdates() {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement("UPDATE my_orders SET status = ? WHERE order_id = ?");

            for (int i = 0; i < model.getRowCount(); i++) {
                String originalStatus = (String) model.getValueAt(i, 4);
                String newStatus = (String) model.getValueAt(i, 5);

                if (!originalStatus.equals(newStatus)) {
                    int orderId = (int) model.getValueAt(i, 0);
                    pstmt.setString(1, newStatus);
                    pstmt.setInt(2, orderId);
                    pstmt.addBatch();
                }
            }

            pstmt.executeBatch();
            conn.commit();
            JOptionPane.showMessageDialog(this, "Order statuses updated successfully!");
            model.setRowCount(0);
            loadOrders();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating orders: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Rollback failed: " + ex.getMessage());
            }
        }
    }
}
