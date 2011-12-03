package se.axisandandroids.client.service.networking;

import java.io.IOException;

import se.axisandandroids.buffer.PriorityFrameBuffer;
import se.axisandandroids.networking.UDP_ClientConnection;
import se.lth.cs.fakecamera.Axis211A;

public class ImageReceiver extends Thread {
	
	private UDP_ClientConnection c;
	private PriorityFrameBuffer frame_buffer;		
	protected final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	
	public ImageReceiver(UDP_ClientConnection c, PriorityFrameBuffer frame_buffer) {
		this.c = c;
		this.frame_buffer = frame_buffer;
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}

	public void run() {
		while (!interrupted() && c.isConnected()) {			
			int len = 0;
			try {
				len = c.recvImage(jpeg);
			} catch (IOException e) {
				System.err.println("Failed to get image. Skipping this.");
				e.printStackTrace();
			}		
			frame_buffer.put(jpeg, len);	
		}
	}
	
	public void interrupt() {
		super.interrupt();
	}
	
}
