package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.client.DisplayMonitor;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.ReceiveThreadSkeleton;
import se.lth.cs.fakecamera.Axis211A;

public class ClientReceiveThread extends ReceiveThreadSkeleton {

	private DisplayMonitor dm;
	private FrameBuffer fb;
	private byte[] img = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	public ClientReceiveThread(Connection c, DisplayMonitor dm, FrameBuffer fb) {
		super(c);
		this.dm = dm;	// Display Monitor
		this.fb = fb;	// FrameBuffer belonging to DisplayThread
	}

	protected void handleImage() {
		int len = 0;
		try {
			len = c.recvImage(img);
		} catch (IOException e) {
			System.err.println("Failed to get image. Skipping this.");
			e.printStackTrace();
			return;
		}

		// Post img to displayThreads buffer
			
		fb.put(img); // copy img ???
	}

	protected void handleSyncMode() {
		int sync_mode = -1;
		try {
			sync_mode = c.recvSyncMode();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		if (sync_mode != -1) {
			dm.setSyncMode(sync_mode);
		}
	}

	protected void handleDispMode() {
		int disp_mode = -1;
		try {
			disp_mode = c.recvDisplayMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (disp_mode != -1) {
			dm.setDispMode(disp_mode);			
			if (disp_mode == Protocol.DISP_MODE.MOVIE) {
				// FORWARD TO ALL OTHER CAMERAS
				
			}
		}
	}

	protected void handleConnected() {

	}

}
