package food;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    private JTextField txtUsername, txtEmail;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnBack;
    private Image backgroundImage;

    public RegisterPage() {
        setTitle("Online Food Ordering System - Register");
        setSize(800, 570);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image (update your image path)
        backgroundImage = new ImageIcon("C:\\Users\\Nova\\Documents\\NetBeansProjects\\food\\f2.jpg").getImage();

        // Custom panel to paint background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Create Your Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUsername = new JLabel("Username:");
        styleLabel(lblUsername);
        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(18);
        styleTransparentTextField(txtUsername);
        gbc.gridx = 1;
        gbc.gridy = 1;
        backgroundPanel.add(txtUsername, gbc);

        JLabel lblEmail = new JLabel("Email:");
        styleLabel(lblEmail);
        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(lblEmail, gbc);

        txtEmail = new JTextField(18);
        styleTransparentTextField(txtEmail);
        gbc.gridx = 1;
        gbc.gridy = 2;
        backgroundPanel.add(txtEmail, gbc);

        JLabel lblPassword = new JLabel("Password:");
        styleLabel(lblPassword);
        gbc.gridx = 0;
        gbc.gridy = 3;
        backgroundPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(18);
        styleTransparentPasswordField(txtPassword);
        gbc.gridx = 1;
        gbc.gridy = 3;
        backgroundPanel.add(txtPassword, gbc);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        styleLabel(lblConfirmPassword);
        gbc.gridx = 0;
        gbc.gridy = 4;
        backgroundPanel.add(lblConfirmPassword, gbc);

        txtConfirmPassword = new JPasswordField(18);
        styleTransparentPasswordField(txtConfirmPassword);
        gbc.gridx = 1;
        gbc.gridy = 4;
        backgroundPanel.add(txtConfirmPassword, gbc);

        btnRegister = new JButton("Register");
        styleButton(btnRegister);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(btnRegister, gbc);

        btnBack = new JButton("Back to Login");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        backgroundPanel.add(btnBack, gbc);

        // Action listeners
        btnRegister.addActionListener(e -> registerUser());
        btnBack.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
    }

    private void styleTransparentTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(Color.WHITE);
        textField.setOpaque(false); // Make it fully transparent
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        textField.setCaretColor(Color.WHITE);
    }

    private void styleTransparentPasswordField(JPasswordField passwordField) {
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setForeground(Color.WHITE);
        passwordField.setOpaque(false); // Make it fully transparent
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
        passwordField.setCaretColor(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 38));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void registerUser() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginPage().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
