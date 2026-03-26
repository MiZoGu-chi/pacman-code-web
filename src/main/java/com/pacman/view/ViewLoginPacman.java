package com.pacman.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ViewLoginPacman extends JPanel {
	
    private static final long serialVersionUID = 1L;
    
    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private JButton buttonLogin;
    private JButton buttonRegisterRedirect;

    public ViewLoginPacman() {
        
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.BLACK); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelTitle = new JLabel("PACMAN LOGIN", SwingConstants.CENTER);
        labelTitle.setForeground(Color.YELLOW);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(labelTitle, gbc);

        JLabel labelUser = new JLabel("Email:");
        labelUser.setForeground(Color.WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(labelUser, gbc);

        textFieldUsername = new JTextField(15);
        gbc.gridx = 1;
        add(textFieldUsername, gbc);

        JLabel labelPass = new JLabel("Password:");
        labelPass.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        add(labelPass, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        buttonLogin = new JButton("Connexion");
        buttonLogin.setBackground(Color.YELLOW);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(buttonLogin, gbc);

        buttonRegisterRedirect = new JButton("Pas de compte ? S'inscrire");
        buttonRegisterRedirect.setContentAreaFilled(false);
        buttonRegisterRedirect.setBorderPainted(false);
        buttonRegisterRedirect.setForeground(Color.CYAN);
        gbc.gridy = 4;
        add(buttonRegisterRedirect, gbc);
    }

    public String getUsername() { return textFieldUsername.getText(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    
    public void addLoginListener(ActionListener listener) {
        buttonLogin.addActionListener(listener);
    }

    public void addRegisterRedirectListener(ActionListener listener) {
        buttonRegisterRedirect.addActionListener(listener);
    }
}