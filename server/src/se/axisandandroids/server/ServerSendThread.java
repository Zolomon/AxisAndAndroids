package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.ClockSync;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.Protocol;
import se.axisandandroids.networking.SendThreadSkeleton;
import se.lth.cs.cameraproxy.Axis211A;


/**
 * SendThread for server sends images put in its FrameBuffer and commands
 * put in its mailbox CircularBuffer.
 * @author jgrstrm
 * @author zol
 * @author fattony
 * @author calliz
 */
public class ServerSendThread extends SendThreadSkeleton {

	protected int BUFFERSIZE = 5;
	protected int INITIAL_BUFFERWAIT_MS = 0;
	protected int COMMAND_BUFFERSIZE = 40;
	protected final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;

	public final CircularBuffer mailbox; 	// Command mailbox for this ServerSendThread.
	public final FrameBuffer frame_buffer; 	// Image mailbox
	private final byte[] jpeg = new byte[FRAMESIZE];

	
	// In a multi-client setup a list with subscribing clients connection
	// objects would be appropriate or some MultiConnection object.

	/**
	 * Create ServerSendThread with connection c.
	 * @param c, Connection object over which to send images and commands. 	 
	 */
	public ServerSendThread(Connection c) {
		super(c);
		mailbox = new CircularBuffer(COMMAND_BUFFERSIZE);
		frame_buffer = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
	}
	
	public void run() {
		frame_buffer.awaitBuffered(INITIAL_BUFFERWAIT_MS);
		while (!interrupted()) {
			if (c.isConnected())
				perform();
		}
	}	

	protected void perform() {
		// 1) Check for message with commands.
		Object command = mailbox.tryGet();

		while (command != null) {
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
			command = mailbox.tryGet();
		}

		// 3) Wait for image message.
		int len = frame_buffer.get(jpeg);  // No busy wait, because this method is blocking :P
		
		try {
			// 4) Send Image via connection.
			if (c.isConnected())
				c.sendImage(jpeg, 0, len);
		} catch (IOException e) { // ACTION
			System.err.println("Send Fail.");
			e.printStackTrace();
			System.out.println("Disconnection this Connection");
			c.disconnect();
			System.exit(1);
		}
	}

}
