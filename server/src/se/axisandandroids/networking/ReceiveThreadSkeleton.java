package se.axisandandroids.networking;

import java.io.IOException;


public class ReceiveThreadSkeleton extends Thread {

	protected Connection c;

	public ReceiveThreadSkeleton(Connection c) {
		this.c = c;
	}

	public void run() {
		while (!interrupted()) {
			try {
				recvCommand();
			} catch (IOException e) {
				System.err.println("IO error");
				System.exit(1);
			}
		}
	}
	
	private void recvCommand() throws IOException {
		int cmd = c.recvInt();		
		switch (cmd) {
		case Protocol.COMMAND.IMAGE: 	
			handleImage();
			break;
		case Protocol.COMMAND.SYNC_MODE:
			handleSyncMode();
			break;
		case Protocol.COMMAND.DISP_MODE:
			handleDispMode();
			break;
		case Protocol.COMMAND.CONNECTED:
			handleConnected();
			break;
		default:
			break;						
		}
	}
	
		
	/* Subclass and override following methods ----------------------------- */
	
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
	
	/* ---------------------------------------------------------------------- */
	
}
