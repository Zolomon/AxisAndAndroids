package se.axisandandroids.server;

import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.networking.ReceiveThreadSkeleton;
import se.axisandandroids.networking.UDP_ServConnection;



/**
 * Receive thread for server is responsible for responding on commands, eg.
 * display mode changes, from the client.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ServerReceiveThread extends ReceiveThreadSkeleton {

	private CameraMonitor camera_monitor;


	/**
	 * Create a ServerReceiveThread, responsible for fetching commands via
	 * Connection object and perform appropriate actions.
	 * @param c, Connection object shared with a corresponding ServerSendThread.
	 * @param camera_monitor, camera monitor synchronizing server settings.
	 */
	public ServerReceiveThread(UDP_ServConnection c, CameraMonitor camera_monitor) {
		super(c);
		this.camera_monitor = camera_monitor;
	}
	
	public void run() {
		while (!interrupted() && !camera_monitor.getDisconnect()) {						
			recvCommand();			
		}
	}	
		
	protected void handleDispMode() {
		int disp_mode = c.recvDisplayMode();	
		camera_monitor.setDisplayMode(disp_mode);
	}

	protected void handleConnected() {
		System.out.println("Client send thread registered as connected...");
		camera_monitor.setConnected();
	}

	protected void handleDisconnect() {
		System.out.println("Disconnect is requested...");
		camera_monitor.setDisconnect(true);
	}

	protected void handleClockSync() {
		byte[] T = new byte[6];
		c.recvBytes(T, 6);
		long recvTime = System.currentTimeMillis();
		camera_monitor.sync_clocks(ClockSync.bytesToLong(T), recvTime);
	}

}
