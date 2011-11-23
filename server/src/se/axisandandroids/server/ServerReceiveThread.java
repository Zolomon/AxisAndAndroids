package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.ReceiveThreadSkeleton;


public class ServerReceiveThread extends ReceiveThreadSkeleton {

	private CameraMonitor camera_monitor;
	
	
	
	public ServerReceiveThread(Connection c, 
							   CameraMonitor camera_monitor) {
		super(c);
		this.camera_monitor = camera_monitor;
	}	
	
	protected void handleImage() {

		/* Instruct CameraThread via CameraMonitor */

		// Since the server pushes, nothing to be done here. If we don't require
		// a request for image to make a client a subscriber in some future
		// multi client setup.
	}

	// Unnecessary !!!
	protected void handleSyncMode() {
			int sync_mode;
			try {
				sync_mode = c.recvSyncMode();
			} catch (IOException e) {
				System.err.println("Counld not receive sync mode.");
				e.printStackTrace();
			}
			
			/* Instruct CameraThread via CameraMonitor */
					
	}
	
	protected void handleDispMode() {
		int disp_mode = Protocol.COMMAND.NOTOK;
		
		try {
			disp_mode = c.recvDisplayMode();
		} catch (IOException e) {
			System.err.println("Counld not receive disp mode.");
			e.printStackTrace();
		}

		/* Instruct CameraThread via CameraMonitor */

		if (disp_mode !=  Protocol.COMMAND.NOTOK) {
			camera_monitor.setDisplayMode(disp_mode);
		}
	}

	protected void handleConnected() {
		/* Instruct CameraThread via CameraMonitor */
		
		// connect / disconnect ?
		// Clean disconnect at least ?
	}

}
