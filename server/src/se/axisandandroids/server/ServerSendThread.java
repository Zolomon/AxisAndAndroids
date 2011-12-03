package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
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
	public ServerSendThread(UDP_ServConnection c) {
		this.c = c;
		mailbox = new CircularBuffer(COMMAND_BUFFERSIZE);
		frame_buffer = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
		imgPusher = new ImagePusher(c, frame_buffer);
	}

	public void run() {	
		imgPusher.start();

		while (!interrupted() && c.isConnected()) {
			// 1) Check for message with commands.
			Object command = mailbox.get();
			if (command != null) {
				try {
					// 2) Send commands via connection object.
					if (command instanceof ModeChange) {
						System.out.println("Server Sending Mode Change.");
						if (c.isConnected()) {
							c.sendInt(((ModeChange) command).cmd);
							c.sendInt(((ModeChange) command).mode);
						}
					} else if (command instanceof ClockSync) {
						c.sendInt(((Command) command).cmd);
					} else if (command instanceof Command) {
						c.sendInt(((Command) command).cmd);
					}
				} catch (IOException e) {
					System.err.println("Send Fail."); // ACTION
					e.printStackTrace();
					System.out.println("Disconnection this Connection");
					c.disconnect();
					System.exit(1);
				}	
			}
		} // end while
		interrupt();
	} // end run	


	public void interrupt() {
		imgPusher.interrupt();
		super.interrupt();
	}
}

