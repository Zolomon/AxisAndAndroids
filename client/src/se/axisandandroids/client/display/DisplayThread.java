package se.axisandandroids.client.display;

import se.axisandandroids.buffer.FrameBuffer;
import se.lth.cs.fakecamera.Axis211A;

public class DisplayThread extends Thread {
	
	
	private final int BUFFERSIZE = 10;
	private final int FRAMESIZE = Axis211A.IMAGE_BUFFER_SIZE;
	
	public FrameBuffer mailbox;
	
	public DisplayThread() {
		mailbox = new FrameBuffer(BUFFERSIZE, FRAMESIZE);
	}
	
	@Override
	public void run() {
		
	}
}
