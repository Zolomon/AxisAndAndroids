package se.axisandandroids.desktop.display;


import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.display.DisplayThreadSkeleton;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.lth.cs.fakecamera.Axis211A;


public class DesktopDisplayThread extends DisplayThreadSkeleton {

	private GUI gui;	
	protected final int BUFFERSIZE = 30;
	
	public DesktopDisplayThread(DisplayMonitor disp_monitor) {
		super(disp_monitor);
		gui = new GUI();
	}

	protected void showImage(long delay, int len) {				
		//System.out.printf("Delay: %d\n", delay);
		
		// jpeg is Axis211A.IMAGE_BUFFER_SIZE but it seems to works without correction
		gui.refreshImage(jpeg); 
	}

}	



class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	ImagePanel imagePanel;
	JButton button;
	boolean firstCall = true;
	boolean play = true;

	public GUI() {
		super();						
		imagePanel = new ImagePanel();
		button = new JButton("Stop");
		button.addActionListener(new ButtonHandler(this));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(imagePanel, BorderLayout.NORTH);
		this.getContentPane().add(button, BorderLayout.SOUTH);
		this.setLocationRelativeTo(null);
		this.pack();
	}

	public void refreshImage(byte[] jpeg) {				
		imagePanel.refresh(jpeg);		
		if (firstCall) {
			this.pack();
			this.setVisible(true);
			firstCall = false;
		}
	}
} // end class GUI


class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	ImageIcon icon;
	public ImagePanel() {
		super();
		icon = new ImageIcon();
		JLabel label = new JLabel(icon);
		add(label, BorderLayout.CENTER);
		this.setSize(200, 200);
	}
	public void refresh(byte[] data) {
		Image theImage = getToolkit().createImage(data);
		getToolkit().prepareImage(theImage,-1,-1,null);	    
		icon.setImage(theImage);
		icon.paintIcon(this, this.getGraphics(), 5, 5);
	}
}

class ButtonHandler implements ActionListener {
	GUI gui;

	public ButtonHandler(GUI gui) {
		this.gui = gui;
	}
	public void actionPerformed(ActionEvent evt) {
		if (gui.play) gui.play = false;
		else gui.play = true;
	}
}
