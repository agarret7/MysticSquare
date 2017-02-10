import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class MysticSquareCanvas extends Canvas implements Runnable, KeyListener, MouseListener{	
	private final static int TILE_X = 75, TILE_Y = 75;
	private final static int GRID_X = 4, GRID_Y = 4;
	public final static int GRID_PIXELS_X = TILE_X * GRID_X, GRID_PIXELS_Y = TILE_Y * GRID_Y;
	private Thread runThread;
	private Image image[];
	URL imagePath[] = new URL[15];
	
	private List<Point> tiles;
	private Point gap;
	private Point oldGap = new Point(-1,-1);
	
	private boolean won = false, isInMenu = true, stop = true, scrambled = false, isInInstructions = false, addedMove = false;
	private int time0 = 0, time = 0, timePause = 0, t = 0, moving = 0, moves = 0, oldRandomMove = -1;
	
	private Image menuImage = null, instructionsImage = null;
	
	public void paint(Graphics g){
		if(isInMenu)
			DrawMenu(g);
		else if(isInInstructions)
			DrawInstructions(g);
		else if(won)
			DrawEndGame();
		else{
			if(tiles == null){
				tiles = new LinkedList<Point>();
				GenerateTiles();
			}
			
			if(moving != 0){
				if(!addedMove && scrambled){
					moves++;
					addedMove = true;
				}
				if(t < TILE_X){
					for (int p = 0; p < 15; p++){
						//Fills in tiles surrounding the movement.
						if(tiles.get(p) != oldGap){
							g.drawImage(image[p], tiles.get(p).x * TILE_X,
								tiles.get(p).y * TILE_Y, TILE_X, TILE_Y, this);
						}
					}
					//Draws the grid and time during movement.
					DrawGrid(g);
					DrawStats(g);
					//Slides the tile one pixel over.
					Moving(g, t);
					t+=5;
				}else if(t >= TILE_X){
					moving = 0;
					t = 0;
					addedMove = false;
					DrawTiles(g);
					DrawGrid(g);
					DrawStats(g);
					if(scrambled)
						CheckWin();
				}
			}else{
				DrawTiles(g);
				DrawGrid(g);
				if(!stop)
					time = ((int)(System.currentTimeMillis()/1000) - time0);
				DrawStats(g);
			}
		}
		
		if(runThread == null){
			this.addKeyListener(this);
			this.addMouseListener(this);
			runThread = new Thread(this);
			runThread.start();
		}
	}
	public void update(Graphics g){
		//This is the default update method which will contain our double buffering.
		Graphics bufferGraphics;
		BufferedImage buffer = null;
		Dimension d = this.getSize();
		
		buffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		bufferGraphics = buffer.getGraphics();
		bufferGraphics.setColor(this.getBackground());
		bufferGraphics.fillRect(0,0,d.width,d.height);
		bufferGraphics.setColor(this.getForeground());
		paint(bufferGraphics);
		
		//Flip
		
		g.drawImage(buffer, 0, 0, this);
	}
	@Override
	public void run(){
		while(true){
			repaint();
			
			try{
				Thread.currentThread();
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	public void GenerateTiles(){
		tiles.clear();
		gap = new Point(3,3);
		time0 = (int)(System.currentTimeMillis()/1000);
		moves = 0;

		for(int y = 0; y < 4; y++)
			for(int x = 0; x < 4; x++){
				tiles.add(new Point(x,y));
			}
	}
	public void Scramble(){
		for(int x = 0; x < 1000; x++){
			Random rand = new Random();
			int randomMove = rand.nextInt(4);
			
			while((randomMove + 2)%4 == oldRandomMove || (randomMove == 0 && gap.x <= 0)
			|| (randomMove == 1 && gap.y >= 3) || (randomMove == 2 && gap.x >= 3) || (randomMove == 3 && gap.y <= 0))					 
				randomMove = rand.nextInt(4);
			oldRandomMove = randomMove;
			switch(randomMove){
			case 0:
				oldGap = gap;
				gap = (new Point(gap.x - 1, gap.y));
				tiles.set(tiles.indexOf(gap), oldGap);
				break;
			case 1:
				oldGap = gap;
				gap = (new Point(gap.x, gap.y + 1));
				tiles.set(tiles.indexOf(gap), oldGap);
				break;
			case 2:
				oldGap = gap;
				gap = (new Point(gap.x + 1, gap.y));
				tiles.set(tiles.indexOf(gap), oldGap);
				break;
			case 3:
				oldGap = gap;
				gap = (new Point(gap.x, gap.y - 1));
				tiles.set(tiles.indexOf(gap), oldGap);
				break;
			}
		}
		stop = false;
	}
	public void CheckWin(){
		if(CP(0,0,0) && CP(1,1,0) && CP(2,2,0) && CP(3,3,0) && CP(4,0,1) && CP(5,1,1)
		&& CP(6,2,1) && CP(7,3,1) && CP(8,0,2) && CP(9,1,2) && CP(10,2,2) && CP (11,3,2)
		&& CP(12,0,3) && CP(13,1,3) && CP(14,2,3)){
			won = true;
			time = ((int)(System.currentTimeMillis()/1000) - time0);
		}
	}
	public boolean CP(int point, int xAnswer, int yAnswer){
		if(tiles.get(point).x == xAnswer && tiles.get(point).y == yAnswer)
			return true;
		else
			return false;
	}
	
	public void Moving(Graphics g, int t){
		switch(moving){
		case 1:
			g.drawImage(image[tiles.indexOf(oldGap)], gap.x * TILE_X + t + 1, gap.y * TILE_Y + 1, TILE_X - 1, TILE_Y - 1, this);
			break;
		case 2:
			g.drawImage(image[tiles.indexOf(oldGap)], gap.x * TILE_X + 1, gap.y * TILE_Y - t + 1, TILE_X - 1, TILE_Y - 1, this);
			break;
		case 3:
			g.drawImage(image[tiles.indexOf(oldGap)], gap.x * TILE_X - t + 1, gap.y * TILE_Y + 1, TILE_X - 1, TILE_Y - 1, this);
			break;
		case 4:
			g.drawImage(image[tiles.indexOf(oldGap)], gap.x * TILE_X + 1, gap.y * TILE_Y + t + 1, TILE_X - 1, TILE_Y - 1, this);
			break;
		}
	}
	public void DrawMenu(Graphics g){
		if(this.menuImage == null){
			try{
				URL imagePath = MysticSquareCanvas.class.getResource("graphics/mysticSquareMenu.png");
				this.menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		g.drawImage(menuImage, 0, 0, this);
	}
	public void DrawInstructions(Graphics g){
		if(this.instructionsImage == null){
			try{
				URL imagePath = MysticSquareCanvas.class.getResource("graphics/instructions.png");
				this.instructionsImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		g.drawImage(instructionsImage, 0, 0, this);
	}
	public void DrawEndGame(){
		won = false;
		stop = true;
		scrambled = false;
		timePause = time;
		JOptionPane.showMessageDialog(null, "You've won. congratulations!\n\nTotal time - "
		+ time + (time == 1 ? " second\nTotal moves - " : " seconds\nTotal moves - ")
		+ moves + " moves\n\nPress \"r\" to scramble.", "Congratulations!", 1);
		time0 = (int)(System.currentTimeMillis()/1000) - timePause;
	}
	public void DrawStats(Graphics g){
		g.setColor(Color.BLACK);
		g.drawString("Time - " + time, 2, GRID_PIXELS_Y + 15);
		g.drawString("Moves - " + moves, 75, GRID_PIXELS_Y + 15);
		g.drawString("r to scramble", 150, GRID_PIXELS_Y + 15);
		g.drawString("p to pause", 235, GRID_PIXELS_Y + 15);
	}
	public void DrawGrid(Graphics g){
		g.setColor(Color.darkGray);
		//Drawing an outside rectangle.
		g.drawRect(0, 0, GRID_PIXELS_X, GRID_PIXELS_Y);
		
		//Drawing vertical lines.
		for(int x = TILE_X; x < GRID_PIXELS_X; x += TILE_X){
			g.drawLine(x, 0, x, GRID_PIXELS_Y);
		}
		//Drawing horizontal lines.
		for(int y = TILE_Y; y < GRID_PIXELS_Y; y += TILE_Y){
			g.drawLine(0, y, GRID_PIXELS_X, y);
		}
	}
	public void DrawTiles(Graphics g){
		if(this.image == null){
			image = new Image[15];
			try{
				imagePath[0] = MysticSquareCanvas.class.getResource("graphics/one.png");
				imagePath[1] = MysticSquareCanvas.class.getResource("graphics/two.png");
				imagePath[2] = MysticSquareCanvas.class.getResource("graphics/three.png");
				imagePath[3] = MysticSquareCanvas.class.getResource("graphics/four.png");
				imagePath[4] = MysticSquareCanvas.class.getResource("graphics/five.png");
				imagePath[5] = MysticSquareCanvas.class.getResource("graphics/six.png");
				imagePath[6] = MysticSquareCanvas.class.getResource("graphics/seven.png");
				imagePath[7] = MysticSquareCanvas.class.getResource("graphics/eight.png");
				imagePath[8] = MysticSquareCanvas.class.getResource("graphics/nine.png");
				imagePath[9] = MysticSquareCanvas.class.getResource("graphics/ten.png");
				imagePath[10] = MysticSquareCanvas.class.getResource("graphics/eleven.png");
				imagePath[11] = MysticSquareCanvas.class.getResource("graphics/twelve.png");
				imagePath[12] = MysticSquareCanvas.class.getResource("graphics/thirteen.png");
				imagePath[13] = MysticSquareCanvas.class.getResource("graphics/fourteen.png");
				imagePath[14] = MysticSquareCanvas.class.getResource("graphics/fifteen.png");
				
				for(int m = 0; m < 15; m++)
					this.image[m] = Toolkit.getDefaultToolkit().getImage(imagePath[m]);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		for(int p = 0; p < 15; p++){
			g.drawImage(image[p], tiles.get(p).x * TILE_X, tiles.get(p).y * TILE_Y, TILE_X, TILE_Y, this);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		if(moving == 0 && !isInMenu && !isInInstructions){
			switch(e.getKeyCode()){
			case KeyEvent.VK_RIGHT:
				if(gap.x > 0){
					oldGap = gap;
					gap = (new Point(gap.x - 1, gap.y));
					tiles.set(tiles.indexOf(gap), oldGap);
					moving = 1;
				}
				break;
			case KeyEvent.VK_UP:
				if(gap.y < 3){
					oldGap = gap;
					gap = (new Point(gap.x, gap.y + 1));
					tiles.set(tiles.indexOf(gap), oldGap);
					moving = 2;
				}
				break;
			case KeyEvent.VK_LEFT:
				if(gap.x < 3){
					oldGap = gap;
					gap = (new Point(gap.x + 1, gap.y));
					tiles.set(tiles.indexOf(gap), oldGap);
					moving = 3;
				}
				break;
			case KeyEvent.VK_DOWN:
				if(gap.y > 0){
					oldGap = gap;
					gap = (new Point(gap.x, gap.y - 1));
					tiles.set(tiles.indexOf(gap), oldGap);
					moving = 4;
				}
				break;
			case KeyEvent.VK_R:
				Scramble();
				scrambled = true;
				moves = 0;
				time0 = (int)(System.currentTimeMillis()/1000);
				break;
			}
		}
		
		switch(e.getKeyCode()){
		case KeyEvent.VK_ENTER:
			if(isInInstructions)
				isInInstructions = false;
				time0 = (int)(System.currentTimeMillis()/1000) - timePause;
			if(isInMenu){
				isInMenu = false;
				isInInstructions = true;
			}
		break;
		case KeyEvent.VK_P:
			if(!isInInstructions){
				isInInstructions = true;
				timePause = time;
			}
			else{
				isInInstructions = false;
				time0 = (int)(System.currentTimeMillis()/1000) - timePause;
			}
		break;
		}
		
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e){
		if(moving == 0 && !isInMenu && !isInInstructions){
			int mouseX = e.getX() / TILE_X;
			int mouseY = e.getY() / TILE_Y;
			
			if(mouseX >= 0 && mouseX <= 3 && mouseY >= 0 && mouseY <= 3)
				if((mouseX == (gap.x - 1) && mouseY == gap.y) || (mouseX == (gap.x) + 1 && mouseY == gap.y)
				|| (mouseY == (gap.y - 1) && mouseX == gap.x) || (mouseY == (gap.y) + 1 && mouseX == gap.x)){
					oldGap = gap;
					gap = new Point(mouseX, mouseY);
					tiles.set(tiles.indexOf(gap), oldGap);
					if(gap.x < oldGap.x)
						moving = 1;
					if(gap.y > oldGap.y)
						moving = 2;
					if(gap.x > oldGap.x)
						moving = 3;
					if(gap.y < oldGap.y)
						moving = 4;
					
					repaint();
				}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e){}
	@Override 
	public void keyTyped(KeyEvent e){}
	@Override
	public void mouseClicked(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
}