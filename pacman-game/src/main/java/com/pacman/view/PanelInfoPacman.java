package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelInfoPacman extends JPanel {
    
    private JLabel scoreLabel;
    private JLabel livesLabel;

    public PanelInfoPacman() {
        this.setBackground(Color.BLUE);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10)); 
        // à modifier, ne pas garder les scores de départ en dur
        scoreLabel = new JLabel("Score: ");
        livesLabel = new JLabel("Lives: ");

        Font font = new Font("Arial", Font.BOLD, 20);
        scoreLabel.setFont(font);
        scoreLabel.setForeground(Color.WHITE);
        livesLabel.setFont(font);
        livesLabel.setForeground(Color.WHITE);

        // Ajout au panneau
        this.add(scoreLabel);
        this.add(livesLabel);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLives(int lives) {
        livesLabel.setText("Lives: " + lives);
    }
}
