package com.pacman.dao.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JFileChooser;
import javax.swing.JFrame; 
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import controller.AbstractController;
import model.PacmanGame;


public final class ViewPacmanGame extends JFrame implements PropertyChangeListener {


    private final PacmanGame game;
    private final PanelPacmanGame panelPacman;

    private final JMenuBar menuBar;
    private final JMenuItem layoutMenuItem;

    private final PanelInfoPacman panelInfo;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ViewPacmanGame(PacmanGame game, AbstractController controller) {
        this.game = game;

        setTitle("Pacman");
        setSize(new Dimension(700, 700));

        panelPacman = new PanelPacmanGame(game.getMaze());
        panelInfo = new PanelInfoPacman();

        panelInfo.updateScore(game.getScore());
        panelInfo.updateLives(game.getLife());

        Dimension windowSize = getSize(); 
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2 - 350;
        setLocation(dx, dy);

        // Menu
        menuBar = new JMenuBar();
        layoutMenuItem = new JMenuItem("Load a Layout");
        menuBar.add(layoutMenuItem);
        setJMenuBar(menuBar); 
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        layoutMenuItem.addActionListener((ActionEvent e) -> {
            chargerLayout();
        });

        game.addPropertyChangeListener(this);

        setLayout(new BorderLayout()); 
        add(panelInfo, BorderLayout.NORTH);
        add(panelPacman, BorderLayout.CENTER);

        panelPacman.setFocusable(true);
        panelPacman.requestFocusInWindow();

        setVisible(true);
    }    

    public void chargerLayout() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a layout file");
        fileChooser.setCurrentDirectory(new java.io.File("layouts"));

        int res = fileChooser.showOpenDialog(this); 
        if (res == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            try {
                String filename = selectedFile.getName();
                String layoutPath = "layouts/" + filename;
                System.out.println("filename: " + filename);
                System.out.println("path :" + layoutPath);
                
                support.firePropertyChange("layoutChanged", null, layoutPath);

                System.out.println("property change fired");
                
            } catch (Exception e) {
                e.printStackTrace(); 
                System.err.println("ERROR in chargerLayout: " + e.getMessage());
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    if("turn".equals(evt.getPropertyName())) {
        int currentTurn = (int) evt.getNewValue(); 
        
        if (currentTurn <= 0) {
            panelPacman.setGameOver(false);
            panelPacman.setVictory(false);
        }
        update();
    }
        if("PacmanCreated".equals(evt.getPropertyName())) {
            
            // on garde le focus graphique
            SwingUtilities.invokeLater(() -> {
                panelPacman.setFocusable(true);
                panelPacman.requestFocusInWindow();
            });
        }
        if("layoutLoaded".equals(evt.getPropertyName())) {
            panelPacman.setMaze(game.getMaze());
            panelPacman.setPacmans_pos(game.getPacmansPosition()); 
            panelPacman.setGhosts_pos(game.getGhostsPosition()); 
            panelPacman.repaint();
        }
        if("PacmanInvicible".equals(evt.getPropertyName())) {
            // les fantômes deviennent bleus
            panelPacman.setGhostsScarred(true); 
        }
        if("PacmanNormal".equals(evt.getPropertyName())) {
            // les fantômes deviennent blancs
            panelPacman.setGhostsScarred(false); 
        }
        if("ScoreChanged".equals(evt.getPropertyName())) {
            panelInfo.updateScore(game.getScore());
        }
        if("LifeChanged".equals(evt.getPropertyName())) {
            panelInfo.updateLives(game.getLife());
        }
        if ("Victory".equals(evt.getPropertyName())) {
            panelPacman.setVictory(true);
        } 
        if ("GameOver".equals(evt.getPropertyName())) {
            panelPacman.setGameOver(true);
        }
        if("GameStart".equals(evt.getPropertyName())) {
            panelPacman.setLaunch(true);
        }
        if("GameStartFinished".equals(evt.getPropertyName())) {
            panelPacman.setLaunch(false);
        }
    }
    
    public void update() {
        panelPacman.setGhosts_pos(game.getGhostsPosition());
        panelPacman.setPacmans_pos(game.getPacmansPosition());   
        panelPacman.repaint();     
    }

    public void setKeyListener(KeyListener keyListener) {
        panelPacman.addKeyListener(keyListener);
    }
}
