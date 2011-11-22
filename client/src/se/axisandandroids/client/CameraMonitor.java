package se.axisandandroids.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import se.lth.cs.fakecamera.Axis211A;

public class CameraMonitor {
	private Axis211A camera;
	private Bitmap bmp;
	private byte[] jpeg = new byte[Axis211A.IMAGE_BUFFER_SIZE];
	private boolean isConnected;
	
	public CameraMonitor() {
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
}
