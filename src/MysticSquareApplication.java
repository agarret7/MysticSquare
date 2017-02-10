/**
* Made by Austin J. Garrett (10/06/2014)
*/

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MysticSquareApplication extends Frame implements WindowListener{
	private MysticSquareCanvas c;

	public static void main(String[] args){
		MysticSquareApplication f = new MysticSquareApplication();
		f.setSize(MysticSquareCanvas.GRID_PIXELS_X + 30,MysticSquareCanvas.GRID_PIXELS_Y + 65);
		f.setVisible(true);
		f.setLayout(new FlowLayout());
	}
	public MysticSquareApplication(){
		c = new MysticSquareCanvas();
		c.setPreferredSize(new Dimension(MysticSquareCanvas.GRID_PIXELS_X + 1, MysticSquareCanvas.GRID_PIXELS_Y + 20));
		c.setVisible(true);
		c.setFocusable(true);
		this.add(c);
		this.addWindowListener(this);
		this.setSize(getPreferredSize());
	}

	@Override
	public void windowClosing(WindowEvent e){
		dispose();
		System.exit(0);
	}	
	@Override
	public void windowActivated(WindowEvent e){}
	@Override
	public void windowClosed(WindowEvent e){}
	@Override
	public void windowDeactivated(WindowEvent e){}
	@Override
	public void windowDeiconified(WindowEvent e){}
	@Override
	public void windowIconified(WindowEvent e){}
	@Override
	public void windowOpened(WindowEvent e){}
}