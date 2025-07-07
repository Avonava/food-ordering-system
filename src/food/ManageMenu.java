package food;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;

public class ManageMenu extends JFrame {
    private JTextField txtName, txtPrice;
    private JTextArea txtDescription;
    private JTextField txtImagePath;
    private JPanel itemsPanel;
    private Connection conn;

    // Colors
    private final Color PRIMARY_COLOR = new Color(40, 167, 69);
    private final Color SECONDARY_COLOR = new Color(33, 37, 41);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(73, 80, 87);

    public ManageMenu() {
        setTitle("Manage Menu Items");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));

        connectToDatabase();

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Manage Menu Items", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, SECONDARY_COLOR, Color.WHITE);
        btnLogout.addActionListener(e -> logout());
        headerPanel.add(btnLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Add Item Panel
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBackground(BACKGROUND_COLOR);
        addPanel.setBorder(new CompoundBorder(
            new TitledBorder(
                new EmptyBorder(5, 5, 5, 5),
                "Add New Menu Item",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        txtName = new JTextField(20);
        styleTextField(txtName);
        addPanel.add(txtName, gbc);

        // Description Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        txtDescription = new JTextArea(3, 20);
        styleTextArea(txtDescription);
        addPanel.add(new JScrollPane(txtDescription), gbc);

        // Price Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(new JLabel("Price:"), gbc);
        
        gbc.gridx = 1;
        txtPrice = new JTextField(10);
        styleTextField(txtPrice);
        addPanel.add(txtPrice, gbc);

        // Image Path Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPanel.add(new JLabel("Image Path:"), gbc);
        
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        txtImagePath = new JTextField();
        styleTextField(txtImagePath);
        JButton btnBrowse = new JButton("Browse...");
        styleButton(btnBrowse, SECONDARY_COLOR, Color.WHITE);
        btnBrowse.addActionListener(e -> browseImageFile());
        imagePanel.add(txtImagePath, BorderLayout.CENTER);
        imagePanel.add(btnBrowse, BorderLayout.EAST);
        addPanel.add(imagePanel, gbc);

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnAdd = new JButton("Add Item");
        styleButton(btnAdd, PRIMARY_COLOR, Color.WHITE);
        btnAdd.addActionListener(e -> addMenuItem());
        addPanel.add(btnAdd, gbc);

        add(addPanel, BorderLayout.NORTH);

        // Items List Panel
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BACKGROUND_COLOR);
        itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(new CompoundBorder(
            new TitledBorder(
                new EmptyBorder(5, 5, 5, 5),
                "Current Menu Items",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                TEXT_COLOR
            ),
            new EmptyBorder(5, 5, 5, 5)) );
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        loadMenuItems();
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

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    private void connectToDatabase() {
        try {
            conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            dispose();
        }
    }

    private void browseImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image File");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtImagePath.setText(selectedFile.getAbsolutePath());
        }
    }

    private void addMenuItem() {
        String name = txtName.getText().trim();
        String description = txtDescription.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String imagePath = txtImagePath.getText().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO menu_items (name, description, price, image_path) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setString(4, imagePath);
            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();
            loadMenuItems();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        txtName.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtImagePath.setText("");
    }

    private void loadMenuItems() {
        itemsPanel.removeAll();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_id, name, description, price, image_path FROM menu_items");

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String imagePath = rs.getString("image_path");

                JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
                itemPanel.setBackground(CARD_COLOR);
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                JLabel itemLabel = new JLabel("<html><b style='font-size:16px;color:" + 
                    String.format("#%02x%02x%02x", 
                        SECONDARY_COLOR.getRed(), 
                        SECONDARY_COLOR.getGreen(), 
                        SECONDARY_COLOR.getBlue()) + 
                    ";'>" + name + "</b> - <span style='color:" + 
                    String.format("#%02x%02x%02x", 
                        PRIMARY_COLOR.getRed(), 
                        PRIMARY_COLOR.getGreen(), 
                        PRIMARY_COLOR.getBlue()) + 
                    ";'>$" + String.format("%.2f", price) + 
                    "</span><br><span style='font-size:13px;'>" + description + 
                    "</span><br><span style='font-size:12px;color:#6c757d;'>" + 
                    (imagePath.isEmpty() ? "No image" : imagePath) + "</span></html>");
                itemPanel.add(itemLabel, BorderLayout.CENTER);

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                btnPanel.setBackground(CARD_COLOR);
                
                JButton btnEdit = new JButton("Edit");
                styleButton(btnEdit, new Color(108, 117, 125), Color.WHITE);
                btnEdit.addActionListener(e -> showEditDialog(id, name, description, price, imagePath));
                btnPanel.add(btnEdit);

                JButton btnDelete = new JButton("Delete");
                styleButton(btnDelete, new Color(220, 53, 69), Color.WHITE);
                btnDelete.addActionListener(e -> deleteMenuItem(id));
                btnPanel.add(btnDelete);

                itemPanel.add(btnPanel, BorderLayout.EAST);

                itemsPanel.add(itemPanel);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void deleteMenuItem(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this item?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM menu_items WHERE item_id = ?");
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMenuItems();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog(int id, String currentName, String currentDescription, double currentPrice, String currentImagePath) {
        JDialog editDialog = new JDialog(this, "Edit Menu Item", true);
        editDialog.setSize(500, 400);
        editDialog.setLocationRelativeTo(this);
        editDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        editDialog.setLayout(new BorderLayout(10, 10));

        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(BACKGROUND_COLOR);
        editPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        editPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEditName = new JTextField(currentName, 20);
        styleTextField(txtEditName);
        editPanel.add(txtEditName, gbc);

        // Description Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        editPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        JTextArea txtEditDescription = new JTextArea(currentDescription, 3, 20);
        styleTextArea(txtEditDescription);
        editPanel.add(new JScrollPane(txtEditDescription), gbc);

        // Price Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        editPanel.add(new JLabel("Price:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtEditPrice = new JTextField(String.valueOf(currentPrice), 10);
        styleTextField(txtEditPrice);
        editPanel.add(txtEditPrice, gbc);

        // Image Path Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        editPanel.add(new JLabel("Image Path:"), gbc);
        
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        JTextField txtEditImagePath = new JTextField(currentImagePath);
        styleTextField(txtEditImagePath);
        JButton btnBrowse = new JButton("Browse...");
        styleButton(btnBrowse, SECONDARY_COLOR, Color.WHITE);
        btnBrowse.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Image File");
            int result = fileChooser.showOpenDialog(editDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                txtEditImagePath.setText(selectedFile.getAbsolutePath());
            }
        });
        imagePanel.add(txtEditImagePath, BorderLayout.CENTER);
        imagePanel.add(btnBrowse, BorderLayout.EAST);
        editPanel.add(imagePanel, gbc);

        // Button Panel
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton btnSave = new JButton("Save Changes");
        styleButton(btnSave, PRIMARY_COLOR, Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                double price = Double.parseDouble(txtEditPrice.getText().trim());
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE menu_items SET name = ?, description = ?, price = ?, image_path = ? WHERE item_id = ?"
                );
                stmt.setString(1, txtEditName.getText().trim());
                stmt.setString(2, txtEditDescription.getText().trim());
                stmt.setDouble(3, price);
                stmt.setString(4, txtEditImagePath.getText().trim());
                stmt.setInt(5, id);
                stmt.executeUpdate();
                stmt.close();

                JOptionPane.showMessageDialog(editDialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
                loadMenuItems();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(editDialog, "Error updating item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(btnSave);
        
        JButton btnCancel = new JButton("Cancel");
        styleButton(btnCancel, SECONDARY_COLOR, Color.WHITE);
        btnCancel.addActionListener(e -> editDialog.dispose());
        buttonPanel.add(btnCancel);
        
        editPanel.add(buttonPanel, gbc);

        editDialog.add(editPanel, BorderLayout.CENTER);
        editDialog.setVisible(true);
    }

    private void logout() {
        dispose(); // Close this window
        new LoginPage().setVisible(true); // Open login page
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ManageMenu().setVisible(true));
    }
}