package se.axisandandroids.server;


import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.ReceiveThreadSkeleton;



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
	public ServerReceiveThread(Connection c, CameraMonitor camera_monitor) {
		super(c);
		this.camera_monitor = camera_monitor;
	}


	protected void handleImage() {
		// Since the server pushes, nothing to be done here. If we don't require
		// a request for image to make a client a subscriber in some future
		// multi-client setup.
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
