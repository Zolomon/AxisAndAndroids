package se.axisandandroids.desktop.client;

import java.io.IOException;

import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.client.service.networking.ClientReceiveThread;
import se.axisandandroids.desktop.display.DesktopGUI;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;

public class DesktopReceiveThread extends ClientReceiveThread {

	
	protected DesktopGUI gui;
	
	public DesktopReceiveThread(Connection c, 
								DisplayMonitor disp_monitor,
								FrameBuffer frame_buffer,
								DesktopGUI gui) {
		super(c, disp_monitor, frame_buffer);
		this.gui = gui;
	}

	/**
	 * Force a synchronization mode for debugging.
	 */
	protected void handleSyncMode() {
		System.out.println("Handling Sync Mode.");
		try {
			int sync_mode = c.recvSyncMode();
			disp_monitor.setSyncMode(sync_mode);
			gui.refreshSyncButtonText();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * If not handled by disp_monitor, the motion detecting camera send
	 * display mode MOVIE and it is handled here and forwarded to other cameras.
	 */
	protected void handleDispMode() {
		System.out.println("Handling Display Mode.");
		try {
			int disp_mode = c.recvDisplayMode();
			disp_monitor.setDispMode(disp_mode);			
			disp_monitor.postToAllMailboxes(new ModeChange(Protocol.COMMAND.DISP_MODE, disp_mode));
			gui.refreshDispButtonText();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
}
