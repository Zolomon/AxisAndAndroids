package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.ReceiveThreadSkeleton;
import se.lth.cs.fakecamera.Axis211A;

public class ServerReceiveThread extends ReceiveThreadSkeleton {

	private CameraMonitor cm;
	
	public ServerReceiveThread(Connection c, CameraMonitor cm) {
		super(c);
		this.cm = cm;
	}
	
	
	protected void handleImage() {

		/* Instruct CameraThread via CameraMonitor */

		// Since the server pushes, nothing to be done here. If we don't require
		// a request for image to make a client a subscriber in some future
		// multi client setup.
	}

	protected void handleSyncMode() {
			int sync_mode;
			try {
				sync_mode = c.recvSyncMode();
			} catch (IOException e) {
				System.err.println("Counld not receive sync mode.");
				e.printStackTrace();
			}
			
			/* Instruct CameraThread via CameraMonitor */
			
			// Force sync mode. Debug purposes. ???
		
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
			cm.setDisplayMode(disp_mode);
		}
	}

	protected void handleConnected() {
		/* Instruct CameraThread via CameraMonitor */
		
		// connect / disconnect ?
		// Clean disconnect at least ?
	}

}
