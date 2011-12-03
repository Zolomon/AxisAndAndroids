package se.axisandandroids.desktop.display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import apple.dts.samplecode.osxadapter.OSXAdapter;

import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Protocol;



/**
 * Desktop client GUI.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DesktopGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private DisplayMonitor dm;
	private final HashMap<Integer, ImagePanel> imagePanels = new HashMap<Integer, ImagePanel>();

	private JPanel imageAreaPanel;
	private JPanel controlAreaPanel;

	private String[] dispModes = { "Auto", "Idle", "Movie" };
	private String[] syncModes = { "Auto", "Sync", "Async" };

	private JComboBox dispBox;
	private JComboBox syncBox;

	private static final String TITLE = "AxisAndAndroids - Desktop Client";

	
	
	/* FROM OSXADDAPTER SAMPLE PACKAGE 
     *  - http://developer.apple.com/library/mac/samplecode/OSXAdapter/OSXAdapter.zip */
	public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();


	/**
	 * Create a DesktopGUI instance. DisplayThreads have to register to the
	 * DesktopGUI for any action to occur.
	 * @param dm, a DiplayMonitor for synchronizing.
	 */
	public DesktopGUI(DisplayMonitor dm) {
		super(); 
		//super("OSXAdapter"); /* FROM OSXADDAPTER SAMPLE PACKAGE */
		
		this.dm = dm;
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);				
		this.getContentPane().setLayout(new BorderLayout());
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);
		this.addWindowListener(new onWindowClose(this.dm));
		
		/* FROM OSXADDAPTER SAMPLE PACKAGE
		 *  - http://developer.apple.com/library/mac/samplecode/OSXAdapter/OSXAdapter.zip		  
		 * Set up our application to respond to the Mac OS X application menu */
		registerForMacOSXEvents();
	}
	
	/**
	 * Create a DesktopGUI instance. This constructor registers the specified 
	 * DisplayThread automatically. Other DisplayThreads have to register to the
	 * DesktopGUI if they should be included in the setup.
	 * @param dm, a DiplayMonitor for synchronizing.
	 * @param ddt, the DisplayThread.
	 */
	public DesktopGUI(DisplayMonitor dm, DesktopDisplayThread ddt) {
		super(); 
		//super("OSXAdapter"); /* FROM OSXADDAPTER SAMPLE PACKAGE */
		
		this.dm = dm;
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);				
		this.getContentPane().setLayout(new BorderLayout());
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);
		this.registerDisplayThread(ddt);		
		this.addWindowListener(new onWindowClose(this.dm));
				
		/* FROM OSXADDAPTER SAMPLE PACKAGE
		 *  - http://developer.apple.com/library/mac/samplecode/OSXAdapter/OSXAdapter.zip		  
		 * Set up our application to respond to the Mac OS X application menu */
        registerForMacOSXEvents();
	}

	/**
	 * Pack up the GUI before show time. Call this when all DisplayThreads are
	 * registered.
	 */
	public void packItUp() {
		System.out.println("Packing...");
		addComboBoxes();
		this.setLocationRelativeTo(null);
		this.pack();
	}

	private void addComboBoxes() {
		System.out.println("Adding ComboBoxes...");
		dispBox = new JComboBox(dispModes);
		dispBox.addActionListener(new DispModeHandler(this, dm));
		controlAreaPanel.add(dispBox);
		syncBox = new JComboBox(syncModes);
		syncBox.addActionListener(new SyncModeHandler(this, dm));
		controlAreaPanel.add(syncBox);
	}

	/**
	 * Register a DisplayThread to the DesktopGUI.
	 * @param ddt, the DisplayThread.
	 */

	public void registerDisplayThread(DesktopDisplayThread ddt) {
		System.out.println("Register Display Thread");
		ImagePanel imagePanel = new ImagePanel();
		imagePanels.put(ddt.hashCode(), imagePanel);
		imageAreaPanel.add(imagePanel);
	}

	/**
	 * Deregister a DisplayThread from the DesktopGUI.
	 * @param ddt, the DisplayThread.
	 */
	public void deregisterDisplayThread(DesktopDisplayThread ddt) {
		System.out.println("Deregister Display Thread");
		ImagePanel ip = imagePanels.get(ddt.hashCode());
		imageAreaPanel.remove(ip);
		imagePanels.remove(ddt.hashCode());
	}

	private int firstImageCount = 0;


	/**
	 * Resolve special treating of first Image from respective DisplayThread.
	 * @param ddt, DisplayThread.
	 * @param jpeg, the image.
	 * @param delay, the delay of image jpeg.
	 */
	public void firstImage(DesktopDisplayThread ddt, byte[] jpeg, long delay) {
		imagePanels.get(ddt.hashCode()).refresh(jpeg, delay);
		if (++firstImageCount == imagePanels.size()) {
			this.setLocationRelativeTo(null);
			this.pack();
			this.setVisible(true);
		}
	}

	/**
	 * Refresh the image and delay shown for DisplayThread ddt.
	 * @param ddt, a DisplayThread.
	 * @param jpeg, byte array containing the image.
	 * @param delay, the delay.
	 */
	public void refreshImage(DesktopDisplayThread ddt, byte[] jpeg, long delay) {
		imagePanels.get(ddt.hashCode()).refresh(jpeg, delay);
	}

	/**
	 * Called to refresh the shown the shown sync. mode.
	 */
	public void refreshSyncComboBox() {
		if (dm.getSyncMode() == Protocol.SYNC_MODE.AUTO) {
			syncBox.setSelectedIndex(Protocol.SYNC_MODE.AUTO);
		} else if (dm.getSyncMode() == Protocol.SYNC_MODE.ASYNC) {
			syncBox.setSelectedItem(Protocol.SYNC_MODE.ASYNC);
		} else if (dm.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
			syncBox.setSelectedIndex(Protocol.SYNC_MODE.SYNC);
		}
	}

	/**
	 * Called to refresh the shown the shown display mode.
	 */
	public void refreshDispComboBox() {
		if (dm.getDispMode() == Protocol.DISP_MODE.AUTO) {
			dispBox.setSelectedIndex(Protocol.DISP_MODE.AUTO);
		} else if (dm.getDispMode() == Protocol.DISP_MODE.IDLE) {
			dispBox.setSelectedIndex(Protocol.DISP_MODE.IDLE);
		} else if (dm.getDispMode() == Protocol.DISP_MODE.MOVIE) {
			dispBox.setSelectedIndex(Protocol.DISP_MODE.MOVIE);
		}
	}


	/* ----------------------------------------------- INNER CLASSES */

	class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private JPanel iconArea;
		private JLabel delayLabel;
		private ImageIcon icon;

		public ImagePanel() {
			super(new BorderLayout());
			iconArea = new JPanel();
			icon = new ImageIcon();
			JLabel label = new JLabel(icon);
			iconArea.add(label, BorderLayout.CENTER);
			iconArea.setSize(320, 200);
			iconArea.setVisible(true);
			delayLabel = new JLabel("Delay");
			this.add(iconArea, BorderLayout.CENTER);
			this.add(delayLabel, BorderLayout.SOUTH);
			this.setSize(320 + 50, 250);
		}

		public void refresh(byte[] data, long delay) {
			Image theImage = getToolkit().createImage(data);
			getToolkit().prepareImage(theImage, -1, -1, null);
			icon.setImage(theImage);
			icon.paintIcon(this, this.getGraphics(), 5, 5);
			delayLabel.setText("Delay: " + delay);
		}
	}

	class ButtonHandler implements ActionListener {
		@SuppressWarnings("unused")
		private DesktopGUI gui;		
		@SuppressWarnings("unused")
		private DisplayMonitor dm;

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
			if (dispBox.getSelectedIndex() == Protocol.DISP_MODE.AUTO) {
				if (dm.getDispMode() != Protocol.DISP_MODE.AUTO) {
					dm
					.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.AUTO));
					dm.setDispMode(Protocol.DISP_MODE.AUTO);
					dispBox.setSelectedIndex(Protocol.DISP_MODE.AUTO);
					System.out.println("DispMode was changed to: " + dispModes[dm.getDispMode()]);
				}
			}
			else if (dispBox.getSelectedIndex() == Protocol.DISP_MODE.IDLE) {
				if (dm.getDispMode() != Protocol.DISP_MODE.IDLE) {
					dm
					.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.IDLE));
					dm.setDispMode(Protocol.DISP_MODE.IDLE);
					dispBox.setSelectedIndex(Protocol.DISP_MODE.IDLE);
					System.out.println("DispMode was changed to: " + dispModes[dm.getDispMode()]);
				}
			}
			else if (dispBox.getSelectedIndex() == Protocol.DISP_MODE.MOVIE) {
				if (dm.getDispMode() != Protocol.DISP_MODE.MOVIE) {
					dm.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.MOVIE));
					dm.setDispMode(Protocol.DISP_MODE.MOVIE);
					dispBox.setSelectedIndex(Protocol.DISP_MODE.MOVIE);
					System.out.println("DispMode was changed to: " + dispModes[dm.getDispMode()]);
				}
			}
		}
	}

	class SyncModeHandler extends ButtonHandler {
		public SyncModeHandler(DesktopGUI gui, DisplayMonitor dm) {
			super(gui, dm);
		}
		public void actionPerformed(ActionEvent evt) {
			if (syncBox.getSelectedIndex() == Protocol.SYNC_MODE.AUTO) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.AUTO) {
					dm.setSyncMode(Protocol.SYNC_MODE.AUTO);
					syncBox.setSelectedIndex(Protocol.SYNC_MODE.AUTO);
					System.out.println("SyncMode was changed to: " + syncModes[dm.getSyncMode()]);
				}
			}			
			else if (syncBox.getSelectedIndex() ==  Protocol.SYNC_MODE.SYNC) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.SYNC) {
					dm.setSyncMode(Protocol.SYNC_MODE.SYNC);
					syncBox.setSelectedIndex(Protocol.SYNC_MODE.SYNC);
					System.out.println("SyncMode was changed to: " + syncModes[dm.getSyncMode()]);
				}
			}			
			else if (syncBox.getSelectedIndex() == Protocol.SYNC_MODE.ASYNC) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.ASYNC) {
					dm.setSyncMode(Protocol.SYNC_MODE.ASYNC);
					syncBox.setSelectedIndex(Protocol.SYNC_MODE.ASYNC);
					System.out.println("SyncMode was changed to: " + syncModes[dm.getSyncMode()]);
				}
			}
		}
	}
	
	class onWindowClose extends WindowAdapter {
		DisplayMonitor dm;
		public onWindowClose(DisplayMonitor dm) {
			this.dm = dm;
		}
		public void windowClosing(WindowEvent e) {
			System.out.println("Window Closed... disconnect status set to: true.");
        	dm.postToAllMailboxes(new Command(Protocol.COMMAND.DISCONNECT));
			this.dm.setDisconnect(true);			
		}
	}
	
	
	
	/**
	 * FROM OSXADDAPTER SAMPLE PACKAGE
     *  - http://developer.apple.com/library/mac/samplecode/OSXAdapter/OSXAdapter.zip	 
	 * Generic registration with the Mac OS X application menu
	 * Checks the platform, then attempts to register with the Apple EAWT
     * See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
     */
    public void registerForMacOSXEvents() {
        if (MAC_OS_X) {
            try {
                /* Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                   use as delegates for various com.apple.eawt.ApplicationListener methods */
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));               
                //OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
                //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * FROM OSXADDAPTER SAMPLE PACKAGE
     *  - http://developer.apple.com/library/mac/samplecode/OSXAdapter/OSXAdapter.zip
     *  General quit handler; fed to the OSXAdapter as the method to call when a system quit event occurs
     *  A quit event is triggered by Cmd-Q, selecting Quit from the application or Dock menu, or logging out
     */
    public boolean quit() { 
    	/* // For options dialouge do:
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
        	dm.postToAllMailboxes(new Command(Protocol.COMMAND.DISCONNECT));
        	dm.setDisconnect(true);
        }
        return (option == JOptionPane.YES_OPTION);
        */
    	
    	// On second thought: who needs options anyway?
    	dm.postToAllMailboxes(new Command(Protocol.COMMAND.DISCONNECT));
    	dm.setDisconnect(true);
    	return true;
    }


} // end class GUI

