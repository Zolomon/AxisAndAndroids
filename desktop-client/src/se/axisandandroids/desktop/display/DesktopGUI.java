package se.axisandandroids.desktop.display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Protocol;



public class DesktopGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private DisplayMonitor dm;
	private final HashMap<Integer, ImagePanel> imagePanels = new HashMap<Integer, ImagePanel>();

	JPanel imageAreaPanel;
	JPanel controlAreaPanel;

	private JButton syncModeButton;
	private JButton dispModeButton;

	
	public DesktopGUI(DisplayMonitor dm) {
		super();
		this.dm = dm;
		
		this.getContentPane().setLayout(new BorderLayout());				
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));		
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);		
	}

	public DesktopGUI(DisplayMonitor dm, DesktopDisplayThread ddt) {
		super();
		this.dm = dm;
		
		this.getContentPane().setLayout(new BorderLayout());				
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));		
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);
				
		this.registerDisplayThread(ddt);			
	}
	
	public void packItUp() {
		System.out.println("Packing...");
		addButtons();
		this.setLocationRelativeTo(null);				
		this.pack();
	}
	
	private void addButtons() {
		System.out.println("Adding buttons...");

		dispModeButton = new JButton("Display Mode");
		dispModeButton.addActionListener(new DispModeHandler(this, dm));
		controlAreaPanel.add(dispModeButton);

		syncModeButton = new JButton("Sync Mode");
		syncModeButton.addActionListener(new SyncModeHandler(this, dm));
		controlAreaPanel.add(syncModeButton);
	}

	public void registerDisplayThread(DesktopDisplayThread ddt) {		
		System.out.println("Register Display Thread");						
		ImagePanel imagePanel = new ImagePanel();
		imagePanels.put(ddt.hashCode(), imagePanel);			
		imageAreaPanel.add(imagePanel);				
	}

	public void deregisterDisplayThread(DesktopDisplayThread ddt) {
		System.out.println("Deregister Display Thread");
		ImagePanel ip = imagePanels.get(ddt.hashCode());			
		imageAreaPanel.remove(ip);
		imagePanels.remove(ddt.hashCode());		
	}

	private int firstImageCount = 0;
	
	public void firstImage(DesktopDisplayThread ddt, byte[] jpeg, long delay) {
		imagePanels.get(ddt.hashCode()).refresh(jpeg, delay);		
		if (++firstImageCount == imagePanels.size()) { 
			this.setLocationRelativeTo(null);				
			this.pack();
			this.setVisible(true);
		}
	}
	
	public void refreshImage(DesktopDisplayThread ddt, byte[] jpeg, long delay) {				
		imagePanels.get(ddt.hashCode()).refresh(jpeg, delay);
	}
	
	public void refreshSyncButtonText() {
		if (dm.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
			syncModeButton.setText("ASYNC/AUTO");
		} else if (dm.getSyncMode() == Protocol.SYNC_MODE.AUTO) {
			syncModeButton.setText("SYNC/AUTO");
		}
	}
	
	public void refreshDispButtonText() {		
		if (dm.getDispMode() == Protocol.DISP_MODE.MOVIE) {
			dispModeButton.setText("AUTO/IDLE");
		} else if (dm.getDispMode() == Protocol.DISP_MODE.AUTO) {			
			dispModeButton.setText("MOVIE");
		}
	}

	
	/* ----------------------------------------------- INNER CLASSES */

	class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		JPanel iconArea;
		JLabel delayLabel;
		ImageIcon icon;			
		
		public ImagePanel() {
			super(new BorderLayout());
			iconArea = new JPanel();
			icon = new ImageIcon();
			JLabel label = new JLabel(icon);			
			iconArea.add(label, BorderLayout.CENTER);
			iconArea.setSize(200, 200);			
			iconArea.setVisible(true);
			delayLabel = new JLabel("Delay");			
			this.add(iconArea, BorderLayout.CENTER);
			this.add(delayLabel, BorderLayout.SOUTH);
			this.setSize(250, 250);
		}	
		public void refresh(byte[] data, long delay) {
			Image theImage = getToolkit().createImage(data);
			getToolkit().prepareImage(theImage,-1,-1,null);	    
			icon.setImage(theImage);
			icon.paintIcon(this, this.getGraphics(), 5, 5);
			delayLabel.setText("Delay: " + delay);
		}
	}
			
	class ButtonHandler implements ActionListener {
		DesktopGUI gui;
		DisplayMonitor dm;
		public ButtonHandler(DesktopGUI gui, DisplayMonitor dm) {
			this.gui = gui;
			this.dm = dm;
		}	
		public void actionPerformed(ActionEvent evt) {
			System.out.println("Button Pressed");
		}
	}

	class DispModeHandler extends ButtonHandler {		
		public DispModeHandler(DesktopGUI gui, DisplayMonitor dm) { 
			super(gui, dm); 
		}
		public void actionPerformed(ActionEvent evt) {		
			if (dm.getDispMode() != Protocol.DISP_MODE.MOVIE) {
				dm.postToAllMailboxes(new ModeChange(Protocol.COMMAND.DISP_MODE, Protocol.DISP_MODE.MOVIE));
				dm.setDispMode(Protocol.DISP_MODE.MOVIE);
				dispModeButton.setText("AUTO/IDLE");
			} else {
				dm.postToAllMailboxes(new ModeChange(Protocol.COMMAND.DISP_MODE, Protocol.DISP_MODE.AUTO));
				dm.setDispMode(Protocol.DISP_MODE.AUTO);
				dispModeButton.setText("MOVIE");
			}
			System.out.println("DispMode Button was Pressed, DispMode set to: " + dm.getDispMode());		
		}
	}

	class SyncModeHandler extends ButtonHandler {		
		public SyncModeHandler(DesktopGUI gui, DisplayMonitor dm) { 
			super(gui, dm); 
		}		
		public void actionPerformed(ActionEvent evt) {		
			if (dm.getSyncMode() != Protocol.SYNC_MODE.SYNC) {
				dm.setSyncMode(Protocol.SYNC_MODE.SYNC);
				syncModeButton.setText("ASYNC/AUTO");
			} else {
				dm.setSyncMode(Protocol.SYNC_MODE.AUTO);
				syncModeButton.setText("SYNC/AUTO");
			}
			System.out.println("SyncMode Button was Pressed, SyncMode set to: " + dm.getSyncMode());		
		}
	}

} // end class GUI


