package se.axisandandroids.server;


import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.UDP_ServConnection;
import se.lth.cs.cameraproxy.Axis211A;


/**
 * SendThread for server sends images put in its FrameBuffer and commands
 * put in its mailbox CircularBuffer.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ServerSendThread extends Thread {

	protected static final int BUFFERSIZE = 5;
	protected static final int COMMAND_BUFFERSIZE = 5;	
	protected static final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;


	private CameraMonitor camera_monitor;
	public final CircularBuffer mailbox; 	// Command mailbox for this ServerSendThread.
	public final FrameBuffer frame_buffer; 	// Image mailbox
	private UDP_ServConnection c;	
	private ImagePusher imgPusher;

	// In a multi-client setup a list with subscribing clients connection
	// objects would be appropriate or some MultiConnection object.

	/**
	 * Create ServerSendThread with connection c.
	 * @param c, Connection object over which to send images and commands. 	 
	 */
	public ServerSendThread(UDP_ServConnection c, CameraMonitor camera_monitor) {
		this.c = c;
		this.camera_monitor = camera_monitor;
		mailbox = new CircularBuffer(COMMAND_BUFFERSIZE);
		frame_buffer = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
		imgPusher = new ImagePusher(c, frame_buffer, camera_monitor);
	}

	public void run() {	

		mailbox.put(new Command(Protocol.COMMAND.CONNECTED));
		camera_monitor.awaitConnected();

		imgPusher.start();

		while (!interrupted()  && !camera_monitor.getDisconnect()) {
			// 1) Check for message with commands.
			Object command = mailbox.get();
			if (command != null) {
				// 2) Send commands via connection object.
				if (command instanceof ModeChange) {
					System.out.println("Server Sending Mode Change.");
					if (c.isConnected()) {
						c.sendInt(((ModeChange) command).cmd);
						c.sendInt(((ModeChange) command).mode);
					}
				} else if (command instanceof ClockSync ||
						   command instanceof Command) {				
					c.sendInt(((Command) command).cmd);
				}
			}
		} // end while				
		interrupt();
	} // end run	


	public void interrupt() {
		if (camera_monitor.getDisconnect() && c != null) {
			c.sendInt(Protocol.COMMAND.DISCONNECT);
		}
		imgPusher.interrupt();
		camera_monitor.setDisconnect(true);
		super.interrupt();
	}
}

