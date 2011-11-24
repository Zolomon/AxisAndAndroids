package se.axisandandroids.client.display;

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
	private long showtime_old = 0;
	private long timestamp_old;

	public DisplayMonitor() {
		camera = new Axis211A();
	}

	public synchronized long syncFrames(long timestamp) throws InterruptedException {
		if (showtime_old <= 0) return 0;

		long showtime_new = showtime_old + (timestamp - timestamp_old);
		long diffTime = showtime_new - System.currentTimeMillis();
		while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
				wait(diffTime);		
		}


		showtime_old = showtime_new;
		return System.currentTimeMillis() - timestamp; // The real delay
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
