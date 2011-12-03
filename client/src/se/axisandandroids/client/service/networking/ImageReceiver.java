package se.axisandandroids.client.service.networking;

import se.axisandandroids.buffer.PriorityFrameBuffer;
import se.axisandandroids.client.display.DisplayMonitor;
import se.lth.cs.fakecamera.Axis211A;

public class ImageReceiver extends Thread {

	private final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	private UDP_ClientConnection c;
	private PriorityFrameBuffer frame_buffer;
	private DisplayMonitor disp_monitor;

	public ImageReceiver(UDP_ClientConnection c, PriorityFrameBuffer frame_buffer, DisplayMonitor disp_monitor) {
		this.c = c;
		this.frame_buffer = frame_buffer;
		this.disp_monitor = disp_monitor;
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}

	public void run() {
		while (!interrupted() && !disp_monitor.getDisconnect()) {			
			int len = 0;
			len = c.recvImage(jpeg);
			frame_buffer.put(jpeg, len);	
		}
	}

	public void interrupt() {
		super.interrupt();
	}

}
