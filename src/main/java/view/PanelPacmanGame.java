package view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;
import model.Maze;
import model.PositionAgent;



public class PanelPacmanGame extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Color wallColor = Color.BLUE;
	private final Color wallColor2 = Color.CYAN;

	private final double sizePacman = 1.1;

	// on fait une liste de couleurs pour pouvoir différencier les pacmans ds les où ils sont plusieurs. (particuliérement utile si l'un des pacmans est controllé pour le différencier des autres)
	private final Color[] pacmansColors = {
		Color.YELLOW,     // couleur du joueur 1  
		Color.GREEN,      // couleur du joueur 2
		Color.ORANGE,     // ...
		Color.RED,         
		Color.LIGHT_GRAY,  
		Color.GRAY,        
		Color.DARK_GRAY,   
		Color.BLUE,            
		Color.CYAN,             
		Color.WHITE,             
	};
	
	private final Color pacmansInvicibleColor = Color.MAGENTA;


	private final Color ghostsColor = Color.white;
	private final Color ghostScarredColor = Color.BLUE;

	private final double sizeFood = 0.3;
	private final Color colorFood = Color.white;

	private final double sizeCapsule = 0.7;
	private final Color colorCapsule = Color.red;

	private Maze m;

	private ArrayList<PositionAgent> pacmans_pos;
	private ArrayList<PositionAgent> ghosts_pos;

	// pour gérer la couleur des fantôme lorsqu'ils sont effrayé
	private boolean ghostsScarred;

	// pour gérer les messages de départ & victoire & défaite
	private boolean isGameOver = false;
	private boolean isVictory = false;
	private boolean isLaunch = false;
	
	private static int sa = 0;
	private static int fa = 0;

	public PanelPacmanGame(Maze maze) {
		this.m = maze;
		pacmans_pos = this.m.getPacman_start();
		ghosts_pos = this.m.getGhosts_start();
		ghostsScarred = false;
	}

	@Override
	public void paint(Graphics g) {

		int dx = getSize().width;
		int dy = getSize().height;
		g.setColor(Color.black);
		g.fillRect(0, 0, dx, dy);

		int sx = m.getSizeX();
		int sy = m.getSizeY();
		double stepx = dx / (double) sx;
		double stepy = dy / (double) sy;
		double posx = 0;

		for (int x = 0; x < sx; x++) {
			double posy = 0;
			for (int y = 0; y < sy; y++) {
				if (m.isWall(x, y)) {
					g.setColor(wallColor2);
					g.fillRect((int) posx, (int) posy, (int) (stepx + 1),
							(int) (stepy + 1));
					g.setColor(wallColor);
					double nsx = stepx * 0.5;
					double nsy = stepy * 0.5;
					double npx = (stepx - nsx) / 2.0;
					double npy = (stepy - nsy) / 2.0;
					g.fillRect((int) (npx + posx), (int) (npy + posy),
							(int) (nsx), (int) nsy);
				}
				if (m.isFood(x, y)) {
					g.setColor(colorFood);
					double nsx = stepx * sizeFood;
					double nsy = stepy * sizeFood;
					double npx = (stepx - nsx) / 2.0;
					double npy = (stepy - nsy) / 2.0;
					g.fillOval((int) (npx + posx), (int) (npy + posy),
							(int) (nsx), (int) nsy);
				}
				if (m.isCapsule(x, y)) {
					g.setColor(colorCapsule);
					double nsx = stepx * sizeCapsule;
					double nsy = stepy * sizeCapsule;
					double npx = (stepx - nsx) / 2.0;
					double npy = (stepy - nsy) / 2.0;
					g.fillOval((int) (npx + posx), (int) (npy + posy),
							(int) (nsx), (int) nsy);
				}
				posy += stepy;
			}
			posx += stepx;
		}

		for (int i = 0; i < pacmans_pos.size(); i++) {
			PositionAgent pos = pacmans_pos.get(i);
			// on utilise le modulo pour ne jamais être out of bound
			drawPacmans(g, pos.getX(), pos.getY(), pos.getDir(), pacmansColors[i % pacmansColors.length]);		
		}

		for (int i = 0; i < ghosts_pos.size(); i++) {
			PositionAgent pos = ghosts_pos.get(i);
			if (ghostsScarred) {
				drawGhosts(g, pos.getX(), pos.getY(), ghostScarredColor);
			} else {
				drawGhosts(g, pos.getX(), pos.getY(), ghostsColor);
			}
		}

		if (isGameOver) {
			drawMessage(g, "GAME OVER", Color.RED);
		} else if (isVictory) {
			drawMessage(g, "VICTORY !", Color.YELLOW);
		} else if (isLaunch) {
        	drawMessage(g, "READY !", Color.YELLOW);
    	}
	}

	void drawPacmans(Graphics g, int px, int py, int pacmanDirection,
			Color color) {

		// si pacman est en vie
		if((px != -1) || (py != -1)){
		
			int dx = getSize().width;
			int dy = getSize().height;
	
			int sx = m.getSizeX();
			int sy = m.getSizeY();
			double stepx = dx / (double) sx;
			double stepy = dy / (double) sy;
	
			double posx = px * stepx;
			double posy = py * stepy;
	
			g.setColor(color);
			double nsx = stepx * sizePacman;
			double nsy = stepy * sizePacman;
			double npx = (stepx - nsx) / 2.0;
			double npy = (stepy - nsy) / 2.0;
			
	
			if (pacmanDirection == Maze.NORTH) {
				sa = 70;
				fa = -320;
			}
			if (pacmanDirection == Maze.SOUTH) {
				sa = 250;
				fa = -320;
			}
			if (pacmanDirection == Maze.EAST) {
				sa = 340;
				fa = -320;
			}
			if (pacmanDirection == Maze.WEST) {
				sa = 160;
				fa = -320;
			}
		
	
			g.fillArc((int) (npx + posx), (int) (npy + posy), (int) (nsx),
					(int) nsy, sa, fa);
		}

	}

	void drawGhosts(Graphics g, int px, int py, Color color) {

		if((px != -1) || (py != -1)){
			int dx = getSize().width;
			int dy = getSize().height;
	
			int sx = m.getSizeX();
			int sy = m.getSizeY();
			double stepx = dx / (double) sx;
			double stepy = dy / (double) sy;
	
			double posx = px * stepx;
			double posy = py * stepy;
	
			g.setColor(color);
	
			double nsx = stepx * sizePacman;
			double nsy = stepy * sizePacman;
			double npx = (stepx - nsx) / 2.0;
			double npy = (stepy - nsy) / 2.0;
	
			g.fillArc((int) (posx + npx), (int) (npy + posy), (int) (nsx),
					(int) (nsy), 0, 180);
			g.fillRect((int) (posx + npx), (int) (npy + posy + nsy / 2.0 - 1),
					(int) (nsx), (int) (nsy * 0.666));
			g.setColor(Color.BLACK);
			g.fillOval((int) (posx + npx + nsx / 5.0),
					(int) (npy + posy + nsy / 3.0), 4, 4);
			g.fillOval((int) (posx + npx + 3 * nsx / 5.0),
					(int) (npy + posy + nsy / 3.0), 4, 4);
	
			g.setColor(Color.black);
		}

	}

	// dessine les messages
	private void drawMessage(Graphics g, String message, Color color) {
		int dx = getSize().width;
		int dy = getSize().height;

		g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
		java.awt.FontMetrics fm = g.getFontMetrics();
		
		int x = (dx - fm.stringWidth(message)) / 2;
		int y = (dy / 2) + (fm.getAscent() / 2);

		g.setColor(new Color(0, 0, 0, 150)); 
		g.fillRect(x - 10, y - fm.getAscent(), fm.stringWidth(message) + 20, fm.getHeight());

		// Dessin du texte
		g.setColor(color);
		g.drawString(message, x, y);
	}

	public Maze getMaze(){
		return m;
	}
	
	public void setMaze(Maze maze){
		this.m = maze;
	}
	
	public void setGhostsScarred(boolean ghostsScarred) {
		this.ghostsScarred = ghostsScarred;
	}

	public ArrayList<PositionAgent> getPacmans_pos() {
		return pacmans_pos;
	}

	public void setPacmans_pos(ArrayList<PositionAgent> pacmans_pos) {
		this.pacmans_pos = pacmans_pos;				
	}

	public ArrayList<PositionAgent> getGhosts_pos() {
		return ghosts_pos;
	}

	public void setGhosts_pos(ArrayList<PositionAgent> ghosts_pos) {
		this.ghosts_pos = ghosts_pos;
	}	

	public void setGameOver(boolean gameOver) {
		this.isGameOver = gameOver;
	}

	public void setVictory(boolean victory) {
		this.isVictory = victory;
	}

	public void setLaunch(boolean launch) {
		this.isLaunch = launch;
	}
}
