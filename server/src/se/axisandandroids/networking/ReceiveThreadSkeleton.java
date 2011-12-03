package se.axisandandroids.networking;


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
			recvCommand();			
		}
	}
		
	public void interrupt() {
		super.interrupt();
	}
	
	protected void recvCommand() {
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
		case Protocol.COMMAND.DISCONNECT:
			handleDisconnect();
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
	
	}

	protected void handleSyncMode() {
	
	}
	
	protected void handleDispMode() {

	}

	protected void handleConnected() {
		
	}
	
	protected void handleDisconnect() {
		
	}

	protected void handleClockSync() {
		
	}
	
	/* ---------------------------------------------------------------------- */
	
}
