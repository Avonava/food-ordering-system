package food;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> roleComboBox;
    private JButton btnLogin, btnRegister;
    private Image backgroundImage;

    public LoginPage() {
        setTitle("Online Food Ordering System - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image (replace with your path)
        backgroundImage = new ImageIcon("C:\\Users\\Nova\\Documents\\NetBeansProjects\\food\\f3.jpg").getImage();

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Constraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form panel with transparency
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBackground(new Color(255, 255, 255, 0));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("Login to Order Food");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));  // Bigger and bold
        lblTitle.setForeground(Color.WHITE);                     // White color
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold
        lblUsername.setForeground(Color.WHITE);                    // White color
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold text
        txtUsername.setForeground(Color.WHITE);                    // White text
        txtUsername.setOpaque(false);                              // Transparent background
        txtUsername.setBackground(new Color(0,0,0,0));
        txtUsername.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE)); // White underline border
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold
        lblPassword.setForeground(Color.WHITE);                   // White color
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold text
        txtPassword.setForeground(Color.WHITE);                   // White text
        txtPassword.setOpaque(false);                              // Transparent background
        txtPassword.setBackground(new Color(0,0,0,0));
        txtPassword.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE)); // White underline border
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtPassword, gbc);

        // Role selector
        JLabel lblRole = new JLabel("Login as:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold
        lblRole.setForeground(Color.WHITE);                   // White color
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(lblRole, gbc);

        roleComboBox = new JComboBox<>(new String[] { "Customer", "Admin" });
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(roleComboBox, gbc);

        // Buttons
        btnLogin = createStyledButton("Login", new Color(102, 255, 102));
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(btnLogin, gbc);

        btnRegister = createStyledButton("Register", new Color(0, 51, 204));
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(btnRegister, gbc);

        backgroundPanel.add(formPanel);
        setContentPane(backgroundPanel);

        // Login action
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPage.this, "Please enter username and password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = selectedRole.equalsIgnoreCase("Admin")
                        ? "SELECT * FROM admins WHERE username = ? AND password = ?"
                        : "SELECT * FROM users WHERE username = ? AND password = ?";

                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, username);
                    pst.setString(2, password);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(LoginPage.this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            if (selectedRole.equalsIgnoreCase("Admin")) {
                                new AdminDashboard(username).setVisible(true);
                            } else {
                                new CustomerDashboard(username).setVisible(true);
                            }
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(LoginPage.this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginPage.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Register action
        btnRegister.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
