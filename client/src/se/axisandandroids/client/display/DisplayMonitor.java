package se.axisandandroids.client.display;

import java.util.PriorityQueue;

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
	//private final long[] timestamps = new long[2];
		
	private final long DELAY_SYNCMODE_TRESH = 200;
	private PriorityQueue<Long> timestamps = new PriorityQueue<Long>();
	private long showtime_old = 0;
	private long timestamp_old = 0;	
	
	public synchronized long syncFrames(int id, long timestamp) throws InterruptedException {
		
		/* No old showtime exists for ANY frame, display now! */
		if (showtime_old <= 0) return System.currentTimeMillis() - timestamp;
				
		/* Calculate showtime for this thread in relation to what has been shown last. */
		long showtime_new = showtime_old + (timestamp - timestamp_old);				
		long diffTime;	// Time to showtime_new
		
		if (sync_mode == Protocol.SYNC_MODE.ASYNC) {
						
			/* Wait until it is:
			 * 1) The right time.										*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0) {
				wait(diffTime);		
			}
						
		} else if (sync_mode == Protocol.SYNC_MODE.SYNC) {
			
			/* Generalize this for other than 2 DisplayThreads !!! */
			//timestamps[id] = timestamp; // Register timestamp for comparision	
			//long first_timestamp = timestamps[0] < timestamps[1] ? timestamps[0] : timestamps[1];

			timestamps.offer(timestamp); // Register timestamp in queue	
					
			/* Wait until it is:
			 * 1) The right time.
			 * 2) timestamp less than all other timestamps.				*/
			while ((diffTime = showtime_new - System.currentTimeMillis()) > 0 
					&& timestamp > timestamps.peek()) {
				wait(diffTime);		
			}
			
			timestamps.remove(); // This timestamp is done
			//timestamps[id] = Long.MAX_VALUE; // This timestamp is done
			
			notifyAll();
		}
		
		timestamp_old = timestamp;
		showtime_old = showtime_new;

		/* Calculate delay and determine sync mode */
		long delay = System.currentTimeMillis() - timestamp; 	// The real delay
		if (delay > DELAY_SYNCMODE_TRESH) sync_mode = Protocol.SYNC_MODE.ASYNC;	// delay determines sync mode
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
