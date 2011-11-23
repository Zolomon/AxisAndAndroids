package se.axisandandroids.server;

import java.io.IOException;

import se.axisandandroids.buffer.CircularBuffer;
import se.axisandandroids.buffer.Command;
import se.axisandandroids.buffer.ModeChange;
import se.axisandandroids.buffer.Frame;
import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;

public class ServerSendThread extends SendThreadSkeleton {

	protected final int BUFFERSIZE = 10;
	public CircularBuffer mailbox; // Command mailbox for this ServerSendThread.

	// In a multi client setup a list with subscribing clients connection 
	// objects would be appropriate or some MultiConnection object. 

	/**
	 * 
	 * @param c, 
	 */
	public ServerSendThread(Connection c) {
		super(c);
		mailbox = new CircularBuffer(BUFFERSIZE);
	}

	protected void perform() {
		// 1) Wait for message with commands to be put in buffer.
		Object command = mailbox.get();
//		
//		if (command == null) {
//			System.out.println("COMMAND = NULL, SOMETHING IS VERY WRONG");
//		}
//		
		// Possible:
		// 		- image
		//		- motion detected => display mode to movie change

		try {
			// 2) Send commands and/or images via connection object.			
			if (command instanceof Frame) {
				c.sendImage(((Frame) command).x, 0, ((Frame) command).len);
			} else if (command instanceof ModeChange) {
				c.sendInt(((ModeChange) command).cmd);
				c.sendInt(((ModeChange) command).mode);
			} else if (command instanceof Command) {
				c.sendInt(((Command) command).cmd);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	


}
