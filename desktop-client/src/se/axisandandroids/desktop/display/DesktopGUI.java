package se.axisandandroids.desktop.display;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DesktopGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	ImagePanel imagePanel;
	JButton button;
	boolean firstCall = true;
	boolean play = true;

	public DesktopGUI() {
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
	
	public void registerDisplayThread() {
		
	}
	
	public void deregisterDisplayThread() {
		
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
	DesktopGUI gui;

	public ButtonHandler(DesktopGUI gui) {
		this.gui = gui;
	}
	
	public void actionPerformed(ActionEvent evt) {
		System.out.println("Button Pressed");
	}
}



