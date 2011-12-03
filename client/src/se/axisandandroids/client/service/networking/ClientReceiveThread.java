package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.client.display.DisplayMonitor;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.UDP_ClientConnection;
import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.buffer.PriorityFrameBuffer;


/**
 * The client receive thread.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ClientReceiveThread extends Thread {

	protected UDP_ClientConnection c;
	protected ImageReceiver imgRecv;
	protected final DisplayMonitor disp_monitor;
	protected final PriorityFrameBuffer frame_buffer;	
	protected final CircularBuffer sendCommandMailbox;

	/**
	 * Create a ClientReceiveThread. 
	 * @param c, Connection object shared with a corresponding ClientSendThread.
	 * @param disp_monitor, a DisplayMonitor synchronizing different DisplayThreads.
	 * @param frame_buffer, a FrameBuffer object = DisplayThreads image buffer (mailbox). 
	 */
	public ClientReceiveThread(UDP_ClientConnection c, 
			DisplayMonitor disp_monitor, 
			PriorityFrameBuffer frame_buffer, 
			CircularBuffer sendCommandMailbox) {
		this.c = c;
		this.disp_monitor = disp_monitor;	// Display Monitor
		this.frame_buffer = frame_buffer;	// FrameBuffer belonging to DisplayThread
		this.sendCommandMailbox = sendCommandMailbox;				
		this.imgRecv = new ImageReceiver(c, frame_buffer);			
	}

	public void run() {		
		imgRecv.start();
		while (!interrupted() && c.isConnected()) {
			try {
				recvCommand();
			} catch (IOException e) {
				System.err.println("ReceiveThread: Connection Object IO error"); // ACTION
				System.exit(1);
			}
		}
		interrupt();
	}
	
	public void interrupt() {
		imgRecv.interrupt();
		super.interrupt();
	}

	private void recvCommand() throws IOException {
		int cmd = c.recvInt();		
		switch (cmd) {		
		case Protocol.COMMAND.SYNC_MODE:
			handleSyncMode();
			break;
		case Protocol.COMMAND.DISP_MODE:
			handleDispMode();
			break;		
		case Protocol.COMMAND.CLOCK_SYNC:
			handleClockSync();
			break;			
		case Protocol.COMMAND.CONNECTED: // Fall Through!
		case Protocol.COMMAND.IMAGE: 	 // Fall Through!
		default:
			break;						
		}
	}


	/**
	 * Receive an image from a server via the Connection object c, 
	 * put the image in the DisplayThreads buffer.
	 */
	protected void handleImage() {

	}

	/**
	 * Force a synchronization mode for debugging.
	 */
	protected void handleSyncMode() {
		System.out.println("Handling Sync Mode.");
		try {
			int sync_mode = c.recvSyncMode();
			disp_monitor.setSyncMode(sync_mode);
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
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}

	protected void handleClockSync() {
		sendCommandMailbox.put(new ClockSync(System.currentTimeMillis()));
	}

}
