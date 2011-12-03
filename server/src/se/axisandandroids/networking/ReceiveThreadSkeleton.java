package se.axisandandroids.networking;

import java.io.IOException;


/**
 * Superclass for Receive threads. Gets, in its run-loop, a command integer 
 * from its Connection object and chooses an appropriate action. The actions 
 * are meant to be implemented by a subclass. 
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ReceiveThreadSkeleton extends Thread {

	protected Connection c;
	
	public ReceiveThreadSkeleton(Connection c) {
		this.c = c;
	}

	public void run() {
		while (!interrupted() && c.isConnected()) {
			try {
				recvCommand();
			} catch (IOException e) {
				System.err.println("ReceiveThread: Connection Object IO error"); // ACTION
				System.exit(1);
			}
		}
		this.interrupt();
	}
		
	public void interrupt() {
		super.interrupt();
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
		case Protocol.COMMAND.CLOCK_SYNC:
			handleClockSync();
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
	
	protected void handleClockSync() {
		
	}
	
	/* ---------------------------------------------------------------------- */
	
}
