package se.axisandandroids.server;

import se.axisandandroids.networking.Connection;
import se.axisandandroids.networking.SendThreadSkeleton;

public class ServerSendThread extends SendThreadSkeleton {

	protected final int BUFFERSIZE = 10;
	//protected CircularBuffer cb;
	//protected FrameBuffer 	 fb;

	
	public ServerSendThread(Connection c) {
		super(c);
	}

	protected void perform() {
		
		// 1) Wait for message with commands to be put in buffer.
		// 	  Stand-alone buffer or buffer in CameraMonitor?
		
		// Possible:
		// 		- image
		//		- motion detected => display mode to movie change
		
		// 2) Send commands and/or images via connection object.
						
	}
	
}
