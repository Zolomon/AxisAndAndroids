package se.axisandandroids.desktop.client;


import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.buffer.PriorityFrameBuffer;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.client.service.networking.UDP_ClientConnection;
import se.axisandandroids.desktop.display.DesktopGUI;
import se.axisandandroids.networking.Protocol;



/**
 * ReceiveThread with DesktopGUI instance for simpler updating of mode changes.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class DesktopReceiveThread extends ClientReceiveThread {

	protected DesktopGUI gui;


	/**
	 * Create new DesktopReceiveThread.
	 * @param c, connection object to camera server.
	 * @param disp_monitor, display monitor for syncing.
	 * @param frame_buffer, a DisplayThreads image buffer to put received images in.
	 * @param gui, the DesktopGUI.
	 */
	public DesktopReceiveThread(UDP_ClientConnection c, 
			DisplayMonitor disp_monitor,
			PriorityFrameBuffer frame_buffer,
			CircularBuffer sendCommandMailbox,
			DesktopGUI gui) {
		super(c, disp_monitor, frame_buffer, sendCommandMailbox);
		this.gui = gui;
		this.setPriority(MAX_PRIORITY);			
	}

	/**
	 * Force a synchronization mode for debugging.
	 */
	protected void handleSyncMode() {
		System.out.println("Handling Sync Mode.");
		int sync_mode = c.recvSyncMode();
		disp_monitor.setSyncMode(sync_mode);
		gui.refreshSyncComboBox();
	}

	/**
	 * If not handled by disp_monitor, the motion detecting camera send
	 * display mode MOVIE and it is handled here and forwarded to other cameras.
	 */
	protected void handleDispMode() {
		System.out.println("Handling Display Mode.");
		int disp_mode = c.recvDisplayMode();
		disp_monitor.setDispMode(disp_mode);			
		disp_monitor.postToAllMailboxes(new ModeChange(Protocol.COMMAND.DISP_MODE, disp_mode));
		gui.refreshDispComboBox();
	}

}
