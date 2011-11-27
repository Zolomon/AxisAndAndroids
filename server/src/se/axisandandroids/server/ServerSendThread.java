package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;
import se.lth.cs.cameraproxy.Axis211A;


public class ServerSendThread extends SendThreadSkeleton {

	protected int BUFFERSIZE = 3;
	protected int INITIAL_BUFFERWAIT_MS = 10;
	protected int COMMAND_BUFFERSIZE = 3;
	protected final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
		
	public final CircularBuffer mailbox; 		// Command mailbox for this ServerSendThread.
	public final FrameBuffer frame_buffer;		// Image mailbox
	private final byte[] jpeg = new byte[FRAMESIZE];
	
	
	// In a multi client setup a list with subscribing clients connection 
	// objects would be appropriate or some MultiConnection object. 

	/**
	 * 
	 * @param c, 
	 */
	public ServerSendThread(Connection c) {
		super(c);
		mailbox = new CircularBuffer(COMMAND_BUFFERSIZE);
		frame_buffer = new FrameBuffer(BUFFERSIZE, FRAMESIZE);	
	}

	public void run() {
		frame_buffer.awaitBuffered(INITIAL_BUFFERWAIT_MS);
		while (!interrupted()) {
			perform();			
		}		
	}
	
	
	protected void perform() {
		// 1) Check for message with commands.
		Object command = mailbox.tryGet();
		
		if (command != null) {
			try {
				// 2) Send commands via connection object.			
				if (command instanceof ModeChange) {
					System.out.println("Server Sending Mode Change.");
					c.sendInt(((ModeChange) command).cmd);
					c.sendInt(((ModeChange) command).mode);
				} else if (command instanceof Command) {
					c.sendInt(((Command) command).cmd);
				}
			} catch (IOException e) {
				System.err.println("Send Fail.");		// ACTION
				e.printStackTrace();
				System.out.println("Disconnection this Connection");
				c.disconnect();
				System.exit(1);
			}
		}

		// 3) Wait for image message.
		int len = frame_buffer.get(jpeg);

		try {
			// 4) Send Image via connection.
			c.sendImage(jpeg, 0, len);
		} catch (IOException e) {						// ACTION
			System.err.println("Send Fail.");
			e.printStackTrace();			
			System.out.println("Disconnection this Connection");
			c.disconnect();
			System.exit(1);
		}
	}	


}
