package se.axisandandroids.server;

import java.util.LinkedList;
import se.axisandandroids.buffer.FrameBuffer;
import se.axisandandroids.networking.UDP_ServConnection;
import se.lth.cs.fakecamera.Axis211A;

public class ImagePusher extends Thread {

	private static final int INITIAL_BUFFERWAIT_MS = 0;

	private LinkedList<UDP_ServConnection> connections_list;
	private CameraMonitor camera_monitor;
	private FrameBuffer frame_buffer;	
	private final byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];

	public ImagePusher(UDP_ServConnection c, FrameBuffer frame_buffer, CameraMonitor camera_monitor) {
		this.frame_buffer = frame_buffer;
		this.camera_monitor = camera_monitor;
		connections_list = new LinkedList<UDP_ServConnection>();
		connections_list.add(c);
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}

	public ImagePusher(LinkedList<UDP_ServConnection> connections_list, FrameBuffer frame_buffer, CameraMonitor camera_monitor) {
		this.camera_monitor = camera_monitor;
		this.frame_buffer = frame_buffer;
		this.connections_list =  connections_list;
		//this.setPriority((NORM_PRIORITY + MIN_PRIORITY)/2);
	}	

	public void run() {
		camera_monitor.awaitConnected();		
		frame_buffer.awaitBuffered(INITIAL_BUFFERWAIT_MS);

		while (!interrupted()  && !camera_monitor.getDisconnect()) {			
			/* Wait for image message. */
			int len = frame_buffer.get(jpeg); 
			/* Send Image via connections. */					
			for (UDP_ServConnection c : connections_list) {
				c.sendImage(jpeg, 0, len);
			}

		}
	}

	public void interrupt() {
		camera_monitor.setDisconnect(true);
		super.interrupt();
	}

}


