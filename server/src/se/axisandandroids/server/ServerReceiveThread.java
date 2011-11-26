package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.ReceiveThreadSkeleton;


public class ServerReceiveThread extends ReceiveThreadSkeleton {

	private CameraMonitor camera_monitor;

	
	/**
	 * Create a ServerReceiveThread, responsible for fetching commands via
	 * Connection object and perform appropriate actions.
	 * @param c, Connection object shared with a corresponding ServerSendThread.
	 * @param camera_monitor, camera monitor synchronizing server settings.
	 */
	public ServerReceiveThread(Connection c, CameraMonitor camera_monitor) {
		super(c);
		this.camera_monitor = camera_monitor;
	}

	
	protected void handleImage() {

		/* Instruct CameraThread via CameraMonitor */

		// Since the server pushes, nothing to be done here. If we don't require
		// a request for image to make a client a subscriber in some future
		// multi-client setup.
	}

	protected void handleDispMode() {
		try {
			int disp_mode = c.recvDisplayMode();	
			camera_monitor.setDisplayMode(disp_mode);
		} catch (IOException e) {
			System.err.println("Could not receive display mode.");			// ACTION
			e.printStackTrace();
		}
	}

	protected void handleConnected() {
		/* Instruct CameraThread via CameraMonitor */

		// connect / disconnect ?
		// Clean disconnect at least ?
	}

}
