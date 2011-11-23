package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.client.DisplayMonitor;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.ReceiveThreadSkeleton;
import se.axisandandroids.buffer.FrameBuffer;
import se.lth.cs.fakecamera.Axis211A;

public class ClientReceiveThread extends ReceiveThreadSkeleton {

	private DisplayMonitor disp_monitor;
	private FrameBuffer frame_buffer;	
	private byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	public ClientReceiveThread(Connection c, 
							   DisplayMonitor disp_monitor, 
							   FrameBuffer frame_buffer) {
		super(c);
		this.disp_monitor = disp_monitor;	// Display Monitor
		this.frame_buffer = frame_buffer;	// FrameBuffer belonging to DisplayThread
	}

	protected void handleImage() {
		int len = 0;
		try {
			/* Get jpeg written to len first bytes of array jpeg. */
			len = c.recvImage(jpeg);
		} catch (IOException e) {
			System.err.println("Failed to get image. Skipping this.");
			e.printStackTrace();
			return;
		}

		/* Post jpeg to displayThreads buffer */			
		frame_buffer.put(jpeg, len);
	}

	/**
	 * For debugging.
	 */
	protected void handleSyncMode() {
		int sync_mode = -1;
		try {
			sync_mode = c.recvSyncMode();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		if (sync_mode != -1) {
			disp_monitor.setSyncMode(sync_mode);
		}
	}

	/**
	 * If not handled by disp_monitor, the motion detecting camera send
	 * display mode MOVIE and it is handled here and forwarded to other cameras.
	 */
	protected void handleDispMode() {
		int disp_mode = -1;
		try {
			disp_mode = c.recvDisplayMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (disp_mode != -1) {
			disp_monitor.setDispMode(disp_mode);			
			if (disp_mode == Protocol.DISP_MODE.MOVIE) {
				// FORWARD TO ALL OTHER CAMERAS				
				// Put something in the sendthread mailbox.
				//Protocol.COMMAND.DISP_MODE
				//Protocol.DISP_MODE.MOVIE
			}
		}
	}

	protected void handleConnected() {

	}

}
