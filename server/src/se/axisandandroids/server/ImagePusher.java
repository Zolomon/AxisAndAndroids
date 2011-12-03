package se.axisandandroids.server;

import java.io.IOException;
import java.util.LinkedList;

import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.networking.UDP_ServConnection;
import se.lth.cs.fakecamera.Axis211A;

public class ImagePusher extends Thread {

	private static final int INITIAL_BUFFERWAIT_MS = 0;

	private LinkedList<UDP_ServConnection> connections_list;
	private FrameBuffer frame_buffer;	
	private final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	
	public ImagePusher(UDP_ServConnection c, FrameBuffer frame_buffer) {
		this.frame_buffer = frame_buffer;
		connections_list = new LinkedList<UDP_ServConnection>();
		connections_list.add(c);
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}

	public ImagePusher(LinkedList<UDP_ServConnection> connections_list, FrameBuffer frame_buffer) {
		this.frame_buffer = frame_buffer;
		this.connections_list =  connections_list;
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}	
		
	public void run() {
		frame_buffer.awaitBuffered(INITIAL_BUFFERWAIT_MS);

		while (!interrupted() && connections_list.peek().isConnected()) {			
			/* Wait for image message. */
			int len = frame_buffer.get(jpeg); 
			try {
				/* Send Image via connections. */					
				for (UDP_ServConnection c : connections_list) {
					c.sendImage(jpeg, 0, len);
				}
			} catch (IOException e) {
				System.err.println("Send Fail.");
				e.printStackTrace();
				System.out.println("Disconnection this Connection");
				for (UDP_ServConnection c : connections_list) {
					c.disconnect();
				}
				System.exit(1);
			}
		}
	}
	
	public void interrupt() {
		super.interrupt();
	}
	
}


