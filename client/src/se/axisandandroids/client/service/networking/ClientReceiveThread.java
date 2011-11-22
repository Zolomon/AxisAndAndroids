package se.axisandandroids.client.service.networking;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.ReceiveThreadSkeleton;

public class ClientReceiveThread extends ReceiveThreadSkeleton {

	public ClientReceiveThread(Connection c) {
		super(c);
	}
	
	protected void handleImage() {
		//byte[] b = c.recvImage(); // get timestamp etc.
	}

	protected void handleSyncMode() {
		//int sync_mode = c.recvSyncMode();
	}
	
	protected void handleDispMode() {
		//int disp_mode = c.recvDisplayMode();
	}

	protected void handleConnected() {
		
	}
	
}
