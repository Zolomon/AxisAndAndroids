package se.axisandandroids.desktop.display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
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

	private JPanel imageAreaPanel;
	private JPanel controlAreaPanel;

	private String[] dispModes = { "Auto", "Idle", "Movie" };
	private String[] syncModes = { "Auto", "Sync", "Async" };

	private JComboBox dispBox;
	private JComboBox syncBox;

	/**
	 * Create a DesktopGUI instance. DisplayThreads have to register to the
	 * DesktopGUI for any action to occur.
	 * @param dm, a DiplayMonitor for synchronizing.
	 */
	public DesktopGUI(DisplayMonitor dm) {
		super();
		this.dm = dm;

		this.getContentPane().setLayout(new BorderLayout());
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);

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
		this.dm = dm;

		this.getContentPane().setLayout(new BorderLayout());
		imageAreaPanel = new JPanel(new FlowLayout());
		controlAreaPanel = new JPanel(new GridLayout(1, 2));
		this.getContentPane().add(imageAreaPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlAreaPanel, BorderLayout.SOUTH);

		this.registerDisplayThread(ddt);
	}

	/**
	 * Pack up the GUI before show time. Call this when all DisplayThreads are
	 * registered.
	 */
	public void packItUp() {
		System.out.println("Packing...");
		// addButtons();
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

	public void refreshImage(DesktopDisplayThread ddt, byte[] jpeg, long delay) {
		imagePanels.get(ddt.hashCode()).refresh(jpeg, delay);
	}

	public void refreshSyncButtonText() {
		if (dm.getSyncMode() == Protocol.SYNC_MODE.AUTO) {
			syncBox.setSelectedIndex(Protocol.SYNC_MODE.AUTO);
		} else if (dm.getSyncMode() == Protocol.SYNC_MODE.ASYNC) {
			syncBox.setSelectedItem(Protocol.SYNC_MODE.ASYNC);
		} else if (dm.getSyncMode() == Protocol.SYNC_MODE.SYNC) {
			syncBox.setSelectedIndex(Protocol.SYNC_MODE.SYNC);
		}
	}

	public void refreshDispButtonText() {
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

		JPanel iconArea;
		JLabel delayLabel;
		ImageIcon icon;

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
			if (dispBox.getSelectedIndex() == 0) {
				if (dm.getDispMode() != Protocol.DISP_MODE.AUTO) {
					dm
					.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.AUTO));
					dm.setDispMode(Protocol.DISP_MODE.AUTO);
					dispBox.setSelectedIndex(0);
				}
			}

			else if (dispBox.getSelectedIndex() == 1) {
				if (dm.getDispMode() != Protocol.DISP_MODE.IDLE) {
					dm
					.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.IDLE));
					dm.setDispMode(Protocol.DISP_MODE.IDLE);
					dispBox.setSelectedIndex(1);
				}
			}

			else if (dispBox.getSelectedIndex() == 2) {
				if (dm.getDispMode() != Protocol.DISP_MODE.MOVIE) {
					dm.postToAllMailboxes(new ModeChange(
							Protocol.COMMAND.DISP_MODE,
							Protocol.DISP_MODE.MOVIE));
					dm.setDispMode(Protocol.DISP_MODE.MOVIE);
					dispBox.setSelectedIndex(2);
				}
			}

			System.out.println("DispMode was changed to: " + dispModes[dm.getDispMode()]);
		}
	}

	class SyncModeHandler extends ButtonHandler {
		public SyncModeHandler(DesktopGUI gui, DisplayMonitor dm) {
			super(gui, dm);
		}

		public void actionPerformed(ActionEvent evt) {
			if (syncBox.getSelectedIndex() == 0) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.AUTO) {
					dm.setSyncMode(Protocol.SYNC_MODE.AUTO);
					syncBox.setSelectedIndex(0);
				}
			}
			
			else if (syncBox.getSelectedIndex() == 1) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.SYNC) {
					dm.setSyncMode(Protocol.SYNC_MODE.SYNC);
					syncBox.setSelectedIndex(1);
				}
			}
			
			else if (syncBox.getSelectedIndex() == 2) {
				if (dm.getSyncMode() != Protocol.SYNC_MODE.ASYNC) {
					dm.setSyncMode(Protocol.SYNC_MODE.ASYNC);
					syncBox.setSelectedIndex(2);
				}
			}

		

			System.out.println("SyncMode was changed to: " + syncModes[dm.getSyncMode()] );
		}
	}

} // end class GUI

