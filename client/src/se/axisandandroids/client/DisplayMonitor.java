package se.axisandandroids.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.lth.cs.fakecamera.Axis211A;

import se.axisandandroids.networking.Protocol;

public class DisplayMonitor {
	private Axis211A camera;
	private Bitmap bmp;
	private byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	private boolean isConnected;
	
	private int disp_mode = Protocol.DISP_MODE.AUTO;
	private int sync_mode = Protocol.SYNC_MODE.AUTO;
	//public FrameBuffer framebuffer;
	
	public DisplayMonitor() {
		camera = new Axis211A();
	}
	
	public synchronized void connect() {
		isConnected = camera.connect();
		System.out.println("Is connected: " + isConnected);
	}
	
	public synchronized void close() {
		camera.close();
		isConnected = false;
	}

	public boolean isConnected() {
		return isConnected;
	}
		
	public synchronized Bitmap nextImage() {
		int len = camera.getJPEG(jpeg, 0);
		return BitmapFactory.decodeByteArray(jpeg, 0, len);
	}
	
	public synchronized void setDispMode(int disp_mode) {
		this.disp_mode = disp_mode;
	}
	
	public synchronized void setSyncMode(int sync_mode) {
		this.sync_mode = sync_mode;
	}
	
}
