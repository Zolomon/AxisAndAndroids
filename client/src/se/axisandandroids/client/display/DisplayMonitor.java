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
	

	public DisplayMonitor() {
		camera = new Axis211A();
	}

	
	/* Generalize this for other than 2 DisplayThreads !!! */
	private final long[] timestamps = new long[2];
	private final long[] showtime_old = new long[2];
	private final long[] timestamp_old = new long[2];
	
	
	public synchronized long syncFrames(int id, long timestamp) throws InterruptedException {
		
		/* No old showtime exists, display now! */
		if (showtime_old[id] <= 0) return System.currentTimeMillis() - timestamp;
		
		long showtime_new = showtime_old[id] + (timestamp - timestamp_old[id]);
		long diffTime;
		
		if (sync_mode == Protocol.SYNC_MODE.ASYNC) {
						
			/* Wait until it is:
			 * 1) The right time.										*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
				wait(diffTime);		
			}
						
		} else if (sync_mode == Protocol.SYNC_MODE.SYNC) {
			
			/* Generalize this for other than 2 DisplayThreads !!! */
			timestamps[id] = timestamp; // Register timestamp for comparision	
			long first_timestamp = timestamps[0] < timestamps[1] ? timestamps[0] : timestamps[1];

			/* Wait until it is:
			 * 1) The right time.
			 * 2) timestamp less than all other timestamps.				*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0 
					&& timestamp <= first_timestamp) {
				wait(diffTime);		
			}
			
			timestamps[id] = Long.MAX_VALUE; // This timestamp is done
			notifyAll();
		}
		
		timestamp_old[id] = timestamp;
		showtime_old[id] = showtime_new;

		/* Calculate delay and determine sync mode */
		long delay = System.currentTimeMillis() - timestamp; 	// The real delay
		if (delay > 200) sync_mode = Protocol.SYNC_MODE.ASYNC;	// delay determines sync mode
		else sync_mode = Protocol.SYNC_MODE.SYNC;
		return delay; 											// Return delay to show
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
